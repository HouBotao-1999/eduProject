package com.atguigu.eduservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

//swagger需要访问http://localhost:8001/swagger-ui.html
//这是一个启动类
@SpringBootApplication
//设置包的扫描规则，启动类扫描所有com.atguigu的包
//否则启动的时候只扫描当前项目（service）的内容
@ComponentScan(basePackages = {"com.atguigu"})
@EnableDiscoveryClient   //nacos注册
@EnableFeignClients      //Feign调用
public class EduApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(EduApplication.class,args);
    }
}
