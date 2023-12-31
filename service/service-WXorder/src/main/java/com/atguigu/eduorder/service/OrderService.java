package com.atguigu.eduorder.service;

import com.atguigu.eduorder.entity.Order;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 订单 服务类
 * </p>
 *
 * @author testjava
 * @since 2023-06-16
 */
public interface OrderService extends IService<Order> {

    String creatOrders(String courseId, String memberIdByJwtToken);
}
