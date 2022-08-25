package com.alphen.mall.service;

import com.alphen.mall.request.CreateOrderReq;
import com.alphen.mall.vo.OrderVO;
import com.github.pagehelper.PageInfo;

public interface OrderService {
    /*
    * 创建订单信息
    * */
    String create(CreateOrderReq createOrderReq);

    /*
    * 前台查看订单详情
    * */
    OrderVO detail(String orderNo);

    /*
    * 前台给订单信息分页
    * */
    PageInfo listForCustomer(Integer pageNum, Integer pageSize);

    /*
    * 前台取消订单
    * */
    void cancel(String orderNo);

    /*
    * 生成支付二维码
    * */
    String qrcode(String orderNo);

    /*
    * 后台-所有订单列表
    * */
    PageInfo listForAdmin(Integer pageNum, Integer pageSize);

    /*
    * 支付
    * */
    void pay(String orderNo);

    /*
     * 发货
     * */
    void deliver(String orderNo);

    /*
     * 前台 交易完成
     * */
    void finish(String orderNo);
}
