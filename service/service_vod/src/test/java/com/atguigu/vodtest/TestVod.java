package com.atguigu.vodtest;

import com.aliyun.vod.upload.impl.UploadVideoImpl;
import com.aliyun.vod.upload.req.UploadVideoRequest;
import com.aliyun.vod.upload.resp.UploadVideoResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.vod.model.v20170321.GetPlayInfoRequest;
import com.aliyuncs.vod.model.v20170321.GetPlayInfoResponse;
import com.aliyuncs.vod.model.v20170321.GetVideoPlayAuthRequest;
import com.aliyuncs.vod.model.v20170321.GetVideoPlayAuthResponse;

import java.util.List;

public class TestVod {
    public static void main(String[] args) throws Exception {
        String accessKeyId = "LTAI5tKntMyMWS9H6rJZnGNo";
        String accessKeySecret = "3B1kvUp9kz2fCeHhTdpAZyVCXmzw32";
        String title = "ceshi";
        String fileName = "D:/jsj/java_pro/项目资料/1-阿里云上传测试视频/6 - What If I Want to Move Faster.mp4";
        UploadVideoRequest request = new UploadVideoRequest(accessKeyId, accessKeySecret, title, fileName);
        /* 可指定分片上传时每个分片的大小，默认为2M字节 */
        request.setPartSize(2 * 1024 * 1024L);
        /* 可指定分片上传时的并发线程数，默认为1，(注：该配置会占用服务器CPU资源，需根据服务器情况指定）*/
        request.setTaskNum(1);

        UploadVideoImpl uploader = new UploadVideoImpl();
        UploadVideoResponse response = uploader.uploadVideo(request);
        System.out.print("RequestId=" + response.getRequestId() + "\n");  //请求视频点播服务的请求ID
        if (response.isSuccess()) {
            System.out.print("VideoId=" + response.getVideoId() + "\n");
        } else {
            /* 如果设置回调URL无效，不影响视频上传，可以返回VideoId同时会返回错误码。其他情况上传失败时，VideoId为空，此时需要根据返回错误码分析具体错误原因 */
            System.out.print("VideoId=" + response.getVideoId() + "\n");
            System.out.print("ErrorCode=" + response.getCode() + "\n");
            System.out.print("ErrorMessage=" + response.getMessage() + "\n");
        }


    }
    //得到播放的凭证
    public static void getPlayAuth() throws Exception{
        //获取视频的播放凭证
        //初始化对象
        DefaultAcsClient Client = InitObject.initVodClient("LTAI5tKntMyMWS9H6rJZnGNo", "3B1kvUp9kz2fCeHhTdpAZyVCXmzw32");
        // 获得获取视频凭证的request和response对象
        GetVideoPlayAuthRequest request = new GetVideoPlayAuthRequest();
        GetVideoPlayAuthResponse response = new GetVideoPlayAuthResponse();
        //向request对象中设置视频id
        request.setVideoId("b02bed10faa571ed901c0764a0fd0102");
        //获取凭证
        response = Client.getAcsResponse(request);
        System.out.println("playAuth:"+response.getPlayAuth());
    }
    //获取视频的地址
    public static void getPlayUrl() throws Exception{
        //1.根据id获取视频播放地址
        //初始化对象
        DefaultAcsClient Client = InitObject.initVodClient("LTAI5tKntMyMWS9H6rJZnGNo", "3B1kvUp9kz2fCeHhTdpAZyVCXmzw32");
        // 获得request和response对象
        GetPlayInfoRequest request = new GetPlayInfoRequest();
        GetPlayInfoResponse response = new GetPlayInfoResponse();
        //向request对象中设置视频id
        request.setVideoId("5105ab60faa471edadaf6732b68f0102");
        //调用初始化的client，传递request，获取结果
        response = Client.getAcsResponse(request);
        //输出请求结果
        List<GetPlayInfoResponse.PlayInfo> playInfoList = response.getPlayInfoList();
        //播放地址
        for (GetPlayInfoResponse.PlayInfo playInfo : playInfoList) {
            System.out.print("PlayInfo.PlayURL = " + playInfo.getPlayURL() + "\n");
            //Base信息
            System.out.print("VideoBase.Title = " + response.getVideoBase().getTitle() + "\n");

        }
    }

}
