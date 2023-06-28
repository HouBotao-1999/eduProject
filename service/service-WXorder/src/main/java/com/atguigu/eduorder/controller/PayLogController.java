package com.atguigu.eduorder.controller;


import com.atguigu.commonutils.R;
import com.atguigu.eduorder.service.PayLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * <p>
 * 支付日志表 前端控制器
 * </p>
 *
 * @author testjava
 * @since 2023-06-16
 */
@RestController
@RequestMapping("/eduorder/paylog")
public class PayLogController {
    @Autowired
    private PayLogService payLogService;
    //生成微信支付二维码
    @GetMapping("/creatNative/{orderNo}")
    public R creatNative(@PathVariable String orderNo){
        Map map = payLogService.creatNative(orderNo);
        return R.ok().data("map",map);
    }
    //查询支付状态
    @GetMapping("qurryPayStatus/{orderNo}")
    public R qurryPayStatus(@PathVariable String orderNo){
        Map<String,String> map = payLogService.qurryPayStatus(orderNo);
        if(map == null){
            return R.error().message("支付出错了");
        }
        if(map.get("trade_state").equals("SUCCESS")){
            payLogService.updateOrderStatus(map);
            return R.ok().message("支付成功");
        }
        return R.ok().code(25000).message("支付中...");
    }
}

