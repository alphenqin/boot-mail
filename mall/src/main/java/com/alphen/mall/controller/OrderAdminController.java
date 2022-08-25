package com.alphen.mall.controller;

import com.alphen.mall.common.ApiRestResponse;
import com.alphen.mall.service.OrderService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/*
* 后台-订单管理控制器
* */
@RestController
@RequestMapping("/admin/order")
public class OrderAdminController {
    @Autowired
    private OrderService orderService;

    /*
    * 后台订单列表
    * */
    @GetMapping("/list")
    public ApiRestResponse listForAdmin(@RequestParam Integer pageNum, @RequestParam Integer pageSize){
        PageInfo pageInfo = orderService.listForAdmin(pageNum,pageSize);
        return ApiRestResponse.success(pageInfo);
    }

    /*
    * 后台进行发货
    * 订单状态流程：
    * 0-用户已取消
    * 10-未付款
    * 20-已付款
    * 30-已发货
    * 40-交易完成
    * */
    @PostMapping("/delivered")
    public ApiRestResponse delivered(@RequestParam String orderNo){
        orderService.deliver(orderNo);
        return ApiRestResponse.success();
    }
}
