package com.atguigu.eduorder.controller;


import com.atguigu.commonutils.JwtUtils;
import com.atguigu.commonutils.R;
import com.atguigu.eduorder.entity.Order;
import com.atguigu.eduorder.service.OrderService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 订单 前端控制器
 * </p>
 *
 * @author testjava
 * @since 2023-06-16
 */
@RestController
@RequestMapping("/eduorder/order")
@CrossOrigin
public class OrderController {
    @Autowired
    private OrderService orderService;

    //1.生成订单
    @PostMapping("/creatOrder/{courseId}")
    //应该是这样的，但是由于不写前端，request改成string id
//    public R saveOrder(@PathVariable String courseId, HttpServletRequest request){
//        String orderNo = orderService.creatOrders(courseId, JwtUtils.getMemberIdByJwtToken(request));
//        return R.ok().data("orderId",orderNo);
//    }
    public R saveOrder(@PathVariable String courseId, String Id){
        String orderNo = orderService.creatOrders(courseId, Id);
        return R.ok().data("orderId",orderNo);
    }

    //2.根据订单id查询订单信息
    @GetMapping("getOrderInfo/{orderId}")
    public R getOrderInfo(@PathVariable String orderId){
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.eq("order_no",orderId);
        Order order = orderService.getOne(wrapper);

        return R.ok().data("order",order);
    }

}

