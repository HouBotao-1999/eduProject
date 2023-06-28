package com.atguigu.oss.service.Impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.CannedAccessControlList;
import com.atguigu.oss.service.OssService;
import com.atguigu.oss.utils.ConstantPropertiesUtils;
import com.atguigu.servicebase.exceptionhandler.GuliException;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

//这个是接口的实现类,加入Service不然spring不会进行管理
@Service
public class OssServiceImpl implements OssService {
    //上传头像到oss
    @Override
    public String UploadAvatar(MultipartFile file) {
        //获取阿里云存储相关常量
        String endPoint = ConstantPropertiesUtils.END_POINT;
        String accessKeyId = ConstantPropertiesUtils.ACCESS_KEY_ID;
        String accessKeySecret = ConstantPropertiesUtils.ACCESS_KEY_SECRET;
        String bucketName = ConstantPropertiesUtils.BUCKET_NAME;

        try {
            //判断oss实例是否存在：如果不存在则创建，如果存在则获取
            OSS ossClient = new OSSClientBuilder().build(endPoint, accessKeyId, accessKeySecret);

            //获取上传文件流
            InputStream inputStream = file.getInputStream();
            //获取文件的名字
            String FileName = file.getOriginalFilename();
            //在文件名称中加入随机的唯一的值,且吧“-”替换为“”
            String uuid = UUID.randomUUID().toString().replaceAll("-","");
            FileName = uuid+ FileName;
            //把文件按照日期进行分类
            String date = new DateTime().toString("yyyy/MM/dd");
            FileName = date +"/"+FileName;
            //文件上传至阿里云
            ossClient.putObject(bucketName, FileName, inputStream);
            // 关闭OSSClient。
            ossClient.shutdown();
            //获取url地址
            String Url = "https://" + bucketName + "." + endPoint + "/" + FileName;
            return Url;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        }
    }

