package com.atguigu.eduorder.service;

import com.atguigu.eduorder.entity.PayLog;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * 支付日志表 服务类
 * </p>
 *
 * @author testjava
 * @since 2023-06-16
 */
public interface PayLogService extends IService<PayLog> {

    Map creatNative(String orderNo);

    Map<String, String> qurryPayStatus(String orderNo);

    void updateOrderStatus(Map<String, String> map);
}
