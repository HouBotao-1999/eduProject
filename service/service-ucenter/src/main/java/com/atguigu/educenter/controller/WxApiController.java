package com.atguigu.educenter.controller;

import com.atguigu.commonutils.R;
import com.atguigu.educenter.entity.UcenterMember;
import com.atguigu.educenter.service.UcenterMemberService;
import com.atguigu.educenter.utils.ConstantWxUtils;
import com.atguigu.educenter.utils.HttpClientUtils;
import com.atguigu.servicebase.exceptionhandler.GuliException;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

@CrossOrigin
@Controller//注意这里没有配置 @RestController，因为不需要返回数据
@RequestMapping("/educenter/wx")
public class WxApiController {
    @Autowired
    private UcenterMemberService memberService;
    //生成微信扫描二维码
    @GetMapping("login")
    public String getWxCode(){
        // 微信开放平台授权baseUrl
        String baseUrl = "https://open.weixin.qq.com/connect/qrconnect" +
                "?appid=%s" +
                "&redirect_uri=%s" +
                "&response_type=code" +
                "&scope=snsapi_login" +
                "&state=%s" +
                "#wechat_redirect";
        // 回调地址
        String redirectUrl = ConstantWxUtils.WX_OPEN_REDIRECT_URL; //获取业务服务器重定向地址
        try {
            redirectUrl = URLEncoder.encode(redirectUrl, "UTF-8"); //url编码
        } catch (UnsupportedEncodingException e) {
            throw new GuliException(20001, e.getMessage());
        }
        // 防止csrf攻击（跨站请求伪造攻击）
        //String state = UUID.randomUUID().toString().replaceAll("-", "");//一般情况下会使用一个随机数
        String state = "impxsk";//为了让大家能够使用我搭建的外网的微信回调跳转服务器，这里填写你在ngrok的前置域名
        System.out.println("state = " + state);
        // 采用redis等进行缓存state 使用sessionId为key 30分钟后过期，可配置
        //键："wechar-open-state-" + httpServletRequest.getSession().getId()
        //值：satte
        //过期时间：30分钟

        //生成qrcodeUrl
        String qrcodeUrl = String.format(
                baseUrl,
                ConstantWxUtils.WX_OPEN_APP_ID,
                redirectUrl,
                state);
        return "redirect:" + qrcodeUrl;
    }

    //获取扫描人信息
    @GetMapping("callback")
    public String callback(String code,String state){

        try {
            //先获取到code的值，临时票据，类似于验证码
            //拿着code请求微信的固定地址，得到两个值，access_token和openid
            String baseAccessTokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token" +
                    "?appid=%s" +
                    "&secret=%s" +
                    "&code=%s" +
                    "&grant_type=authorization_code";
            //拼接三个参数，id+密钥+code值
            String accessTokenUrl = String.format(baseAccessTokenUrl,
                    ConstantWxUtils.WX_OPEN_APP_ID,
                    ConstantWxUtils.WX_OPEN_APP_SECRET,
                    code);
            String accessTokenInfo = HttpClientUtils.get(accessTokenUrl);
            //利用gson转化为hashmap取出access_token和openid
            Gson gson = new Gson();
            HashMap MapAccessToken = gson.fromJson(accessTokenInfo, HashMap.class);
            String  access_token = (String) MapAccessToken.get("access_token");
            String openid =(String) MapAccessToken.get("openid");
            //拿着access_token和openid再去请求一个维系提供的地址，获取扫码人信息
            //访问微信的资源服务器，获取用户信息
//            String baseUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo" +
//                    "?access_token=%s" +
//                    "&openid=%s";
//            String UserInfoUrl = String.format(
//                    baseUserInfoUrl,
//                    access_token,
//                    openid
//            );
//            //发送请求
//            String UserInfo = HttpClientUtils.get(UserInfoUrl);
//            //去到信息
//            HashMap UserInfoMap = gson.fromJson(UserInfo, HashMap.class);
//            String nickname = (String)UserInfoMap.get("nickname");
//            String headimgurl = (String)UserInfoMap.get("headimgurl");
//            //扫码人信息添加到数据库中
            UcenterMember member = memberService.getOpenidMember(openid);
            if(member == null){
                System.out.println("新用户注册");
                //访问微信的资源服务器，获取用户信息
                String baseUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo" +
                        "?access_token=%s" +
                        "&openid=%s";
                String userInfoUrl = String.format(baseUserInfoUrl, access_token, openid);
                String resultUserInfo = null;
                try {
                    resultUserInfo = HttpClientUtils.get(userInfoUrl);
                    System.out.println("resultUserInfo==========" + resultUserInfo);
                } catch (Exception e) {
                    throw new GuliException(20001, "获取用户信息失败");
                }
                //解析json
                HashMap<String, Object> mapUserInfo = gson.fromJson(resultUserInfo, HashMap.class);
                String nickname = (String)mapUserInfo.get("nickname");
                String headimgurl = (String)mapUserInfo.get("headimgurl");
                //向数据库中插入一条记录
                member = new UcenterMember();
                member.setNickname(nickname);
                member.setOpenid(openid);
                member.setAvatar(headimgurl);
                memberService.save(member);
            }



        } catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:http://localhost:3000";
    }
}

