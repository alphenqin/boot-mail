package com.alphen.mall.controller;

import com.alphen.mall.common.ApiRestResponse;
import com.alphen.mall.request.CreateOrderReq;
import com.alphen.mall.service.OrderService;
import com.alphen.mall.vo.OrderVO;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/*
* 订单控制器
* */
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /*
    * 创建订单
    * */
    @PostMapping("/create")
    public ApiRestResponse create(@RequestBody @Valid CreateOrderReq createOrderReq){
        String orderNo = orderService.create(createOrderReq);
        return ApiRestResponse.success(orderNo);
    }

    /*
    * 查看该订单详情
    * */
    @GetMapping("/detail")
    public ApiRestResponse detail(@RequestParam String orderNo){
        OrderVO orderVO = orderService.detail(orderNo);
        return ApiRestResponse.success(orderVO);
    }

    /*
     * 订单列表
     * */
    @PostMapping("/list")
    public ApiRestResponse list(@RequestParam Integer pageNum,@RequestParam Integer pageSize){
        PageInfo pageInfo = orderService.listForCustomer(pageNum,pageSize);
        return ApiRestResponse.success(pageInfo);
    }

    /*
     * 订单取消
     * */
    @PostMapping("/cancel")
    public ApiRestResponse cancel(@RequestParam String orderNo){
        orderService.cancel(orderNo);
        return ApiRestResponse.success();
    }

    /*
     * 生成支付二维码
     * */
    @PostMapping("/qrcode")
    public ApiRestResponse qrcode(@RequestParam String orderNo){
        String pngAddress = orderService.qrcode(orderNo);
        return ApiRestResponse.success(pngAddress);
    }

    /*
     * 支付接口
     * */
    @GetMapping("/pay")
    public ApiRestResponse pay(@RequestParam String orderNo){
        orderService.pay(orderNo);
        return ApiRestResponse.success();
    }

    /*
     * 交易完成
     * */
    @PostMapping("/finish")
    public ApiRestResponse finish(@RequestParam String orderNo){
        orderService.finish(orderNo);
        return ApiRestResponse.success();
    }

}
