package com.alphen.mall.service.impl;

import com.alphen.mall.common.Constant;
import com.alphen.mall.exception.AlphenMallException;
import com.alphen.mall.exception.AlphenMallExceptionEnum;
import com.alphen.mall.filter.UserFilter;
import com.alphen.mall.model.dao.CartMapper;
import com.alphen.mall.model.dao.OrderItemMapper;
import com.alphen.mall.model.dao.OrderMapper;
import com.alphen.mall.model.dao.ProductMapper;
import com.alphen.mall.model.pojo.Order;
import com.alphen.mall.model.pojo.OrderItem;
import com.alphen.mall.model.pojo.Product;
import com.alphen.mall.request.CreateOrderReq;
import com.alphen.mall.service.CartService;
import com.alphen.mall.service.OrderService;
import com.alphen.mall.service.UserService;
import com.alphen.mall.utils.OrderCodeFactory;
import com.alphen.mall.utils.QRCodeGenerator;
import com.alphen.mall.vo.CartVO;
import com.alphen.mall.vo.OrderItemVO;
import com.alphen.mall.vo.OrderVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.zxing.WriterException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
* 订单Service
* */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserService userService;

    @Value("${file.upload.ip}")
    String ip;

    /*
    * 创建订单信息
    * */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public String create(CreateOrderReq createOrderReq){
        //拿到用户ID
        Integer userId = UserFilter.currentUser.getId();
        //从购物车查找已经勾选的商品
        List<CartVO> cartVOList = cartService.list(userId);
        List<CartVO> cartVOListTemp = new ArrayList<>();
        for(int i = 0;i<cartVOList.size();i++){
            CartVO cartVO = cartVOList.get(i);
            //筛选被勾选的购物车商品
            if(cartVO.getSelected().equals(Constant.Cart.CHECKED)){
                cartVOListTemp.add(cartVO);
            }
        }
        cartVOList = cartVOListTemp;
        //如果购物车已勾选的商品为空，则报错
        if(CollectionUtils.isEmpty(cartVOList)){
            throw new AlphenMallException(AlphenMallExceptionEnum.CART_EMPTY);
        }
        //判断商品是否存在、上下架状态、库存
        validSaleStatusAndStock(cartVOList);
        //把购物车订单对象转为item对象
        List<OrderItem> orderItemList = cartVOListToOrderItemList(cartVOList);
        //扣库存
        for(int i = 0;i<orderItemList.size();i++){
            OrderItem orderItem = orderItemList.get(i);
            //获取当前item对象里的商品id，然后查询到该商品对象
            Product product = productMapper.selectByPrimaryKey(orderItem.getProductId());
            if(product.getStock()<orderItem.getQuantity()){
                throw new AlphenMallException(AlphenMallExceptionEnum.NOT_ENOUGH);
            }
            int stock = product.getStock()-orderItem.getQuantity();
            product.setStock(stock);
            productMapper.updateByPrimaryKeySelective(product);
        }
        //把购物车中的已勾选商品删除
        clean(cartVOList);
        //生成订单
        Order order = new Order();
        //生成订单号，有独立的规则
        String orderNo = OrderCodeFactory.getOrderCode(Long.valueOf(userId));
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setTotalPrice(totalPrice(orderItemList));
        order.setReceiverAddress(createOrderReq.getReceiverAddress());
        order.setReceiverMobile(createOrderReq.getReceiverMobile());
        order.setReceiverName(createOrderReq.getReceiverName());
        order.setOrderStatus(Constant.OrderStatusEnum.NOT_PAID.getCode());
        order.setPostage(0);
        order.setPaymentType(1);
        orderMapper.insertSelective(order);
        //循环保存每个商品到order_item表
        for(int i = 0;i<orderItemList.size();i++){
            OrderItem orderItem = orderItemList.get(i);
            orderItem.setOrderNo(orderNo);
            orderItemMapper.insertSelective(orderItem);
        }
        //返回订单号
        return orderNo;
    }
    //判断该商品是否有效(是否存在/上下架/库存)
    public void validSaleStatusAndStock(List<CartVO> cartVOList){
        for(int i = 0;i<cartVOList.size();i++){
            CartVO cartVO = cartVOList.get(i);
            Product product = productMapper.selectByPrimaryKey(cartVO.getProductId());
            //判断商品是否存在以及是否可售
            if(product == null || product.getStatus().equals(Constant.SaleStatus.NOT_SALE)){
                throw new AlphenMallException(AlphenMallExceptionEnum.NOT_SALE);
            }
            //判断商品库存是否足够
            if(cartVO.getQuantity()>product.getStock()){
                throw new AlphenMallException(AlphenMallExceptionEnum.NOT_ENOUGH);
            }
        }
    }
    //把购物车对象转为item对象
    public List<OrderItem> cartVOListToOrderItemList(List<CartVO> cartVOList){
        List<OrderItem> orderItemList = new ArrayList<>();
        for(int i = 0;i<cartVOList.size();i++){
            OrderItem orderItem = new OrderItem();
            CartVO cartVO = cartVOList.get(i);
            orderItem.setProductId(cartVO.getProductId());
            //记录商品当时的情况
            orderItem.setProductName(cartVO.getProductName());
            orderItem.setProductImg(cartVO.getProductImage());
            orderItem.setUnitPrice(cartVO.getPrice());
            orderItem.setTotalPrice(cartVO.getTotalPrice());
            orderItem.setQuantity(cartVO.getQuantity());
            orderItemList.add(orderItem);
        }
        return orderItemList;
    }
    //把购物车中的已勾选商品删除
    public void clean(List<CartVO> cartVOList){
        for(int i = 0;i<cartVOList.size();i++){
            CartVO cartVO = cartVOList.get(i);
            if(cartVO.getSelected().equals(Constant.Cart.CHECKED)){
                cartMapper.deleteByPrimaryKey(cartVO.getId());
            }
        }
    }
    //一整个订单的全部价格
    public Integer totalPrice(List<OrderItem> orderItemList){
        Integer totalPrices = 0;
        for(int i = 0;i<orderItemList.size();i++){
            OrderItem orderItem = orderItemList.get(i);
            totalPrices +=orderItem.getTotalPrice();
        }
        return totalPrices;
    }

    /*
    * 前台查看订单详情
    * */
    @Override
    public OrderVO detail(String orderNo){
        //获取原始订单对象
        Order order = orderMapper.selectByOrderNo(orderNo);
        //若订单不存在
        if(order == null){
            throw new AlphenMallException(AlphenMallExceptionEnum.NOT_ORDER);
        }
        //若订单存在，判断该订单是否为该用户的订单
        Integer userId = UserFilter.currentUser.getId();
        if(!order.getUserId().equals(userId)){
            throw new AlphenMallException(AlphenMallExceptionEnum.NOT_YOUR_ORDER);
        }
        OrderVO orderVO = getOrderVO(order);
        return orderVO;
    }
    //将Order对象转换为OrderVO对象
    public OrderVO getOrderVO(Order order){
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(order,orderVO);
        //获取Order对应的OrderItemList
        List<OrderItem> orderItemList = orderItemMapper.selectByOrderNo(order.getOrderNo());
        List<OrderItemVO> orderItemVOList = new ArrayList<>();
        for(int i = 0;i<orderItemList.size();i++){
            OrderItem orderItem = orderItemList.get(i);
            OrderItemVO orderItemVO = new OrderItemVO();
            BeanUtils.copyProperties(orderItem,orderItemVO);
            orderItemVOList.add(orderItemVO);
        }
        orderVO.setOrderItemVOList(orderItemVOList);
        //获取用户状态的中文详情
        orderVO.setOrderStatusName(Constant.OrderStatusEnum.code(order.getOrderStatus()).getValue());
        return orderVO;
    }

    /*
    * 前台给订单信息分页
    * */
    @Override
    public PageInfo listForCustomer(Integer pageNum, Integer pageSize){
        PageHelper.startPage(pageNum,pageSize);
        Integer userId = UserFilter.currentUser.getId();
        List<Order> orderList = orderMapper.selectForCustomer(userId);
        List<OrderVO> orderVOList = orderListToOrderVOList(orderList);
        PageInfo pageInfo = new PageInfo(orderList);
        pageInfo.setList(orderVOList);
        return pageInfo;
    }
    //将oderList转为orderVOList
    public List<OrderVO> orderListToOrderVOList(List<Order> orderList){
        List<OrderVO> orderVOList = new ArrayList<>();
        for(int i = 0;i<orderList.size();i++){
            Order order = orderList.get(i);
            OrderVO orderVO = getOrderVO(order);
            orderVOList.add(orderVO);
        }
        return orderVOList;
    }

    /*
    * 前台取消订单
    * */
    @Override
    public void cancel(String orderNo){
        //获取原始订单对象
        Order order = orderMapper.selectByOrderNo(orderNo);
        //若订单不存在
        if(order == null){
            throw new AlphenMallException(AlphenMallExceptionEnum.NOT_ORDER);
        }
        //若订单存在，判断该订单是否为该用户的订单
        Integer userId = UserFilter.currentUser.getId();
        if(!order.getUserId().equals(userId)){
            throw new AlphenMallException(AlphenMallExceptionEnum.NOT_YOUR_ORDER);
        }
        //若当前订单未付款
        if(order.getOrderStatus().equals(Constant.OrderStatusEnum.NOT_PAID.getCode())){
            order.setOrderStatus(Constant.OrderStatusEnum.CANCELED.getCode());
            order.setEndTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);
        } else {
            throw new AlphenMallException(AlphenMallExceptionEnum.WRONG_STATUS_ORDER);
        }
    }

    /*
    * 生成支付二维码
    * */
    @Override
    public String qrcode(String orderNo){
        //获得端口号，从request中得到
        ServletRequestAttributes attributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        //自定义ip，不能从request中获取
        String address = ip+":"+request.getLocalPort();
        //点击该支付地址，即是为该订单的支付接口
        String payUrl = "http://"+address+"/pay?orderNo="+orderNo;
        try {
            QRCodeGenerator.generatorQRCodeImage(
                    payUrl,350,350,Constant.FILE_UPLOAD_DIR+orderNo+".png");
        } catch (WriterException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //得到一个图片地址用于返回图片
        String pngAddress = "http://"+address+"/images/"+orderNo+".png";
        return pngAddress;
    }

    /*
    * 后台-所有订单列表
    * */
    @Override
    public PageInfo listForAdmin(Integer pageNum,Integer pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<Order> orderList = orderMapper.selectAllForAdmin();
        List<OrderVO> orderVOList = orderListToOrderVOList(orderList);
        PageInfo pageInfo = new PageInfo(orderList);
        pageInfo.setList(orderVOList);
        return pageInfo;
    }

    /*
    * 支付
    * */
    @Override
    public void pay(String orderNo){
        //获取原始订单对象
        Order order = orderMapper.selectByOrderNo(orderNo);
        //若订单不存在
        if(order == null){
            throw new AlphenMallException(AlphenMallExceptionEnum.NOT_ORDER);
        }
        //若订单未支付
        if(order.getOrderStatus().equals(Constant.OrderStatusEnum.NOT_PAID.getCode())){
            order.setOrderStatus(Constant.OrderStatusEnum.PAID.getCode());
            order.setPayTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);
        } else {
            //若订单已支付
            throw new AlphenMallException(AlphenMallExceptionEnum.WRONG_STATUS_ORDER);
        }
    }

    /*
     * 发货
     * */
    @Override
    public void deliver(String orderNo){
        //获取原始订单对象
        Order order = orderMapper.selectByOrderNo(orderNo);
        //若订单不存在
        if(order == null){
            throw new AlphenMallException(AlphenMallExceptionEnum.NOT_ORDER);
        }
        //若订单已支付，则发货
        if(order.getOrderStatus().equals(Constant.OrderStatusEnum.PAID.getCode())){
            order.setOrderStatus(Constant.OrderStatusEnum.DELIVERED.getCode());
            order.setDeliveryTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);
        } else {
            //若订单没有支付，以及其他情况
            throw new AlphenMallException(AlphenMallExceptionEnum.WRONG_STATUS_ORDER);
        }
    }

    /*
     * 前台 交易完成
     * */
    @Override
    public void finish(String orderNo){
        //获取原始订单对象
        Order order = orderMapper.selectByOrderNo(orderNo);
        //若订单不存在
        if(order == null){
            throw new AlphenMallException(AlphenMallExceptionEnum.NOT_ORDER);
        }
        //判断订单归属问题
        //如果是普通用户且该订单不是当前用户的订单
        if (!userService.checkAdmin(UserFilter.currentUser) &&
                !order.getUserId().equals(UserFilter.currentUser.getId())) {
            throw new AlphenMallException(AlphenMallExceptionEnum.NOT_YOUR_ORDER);
        }
        //若订单已发货，则交易完成
        if(order.getOrderStatus().equals(Constant.OrderStatusEnum.DELIVERED.getCode())){
            order.setOrderStatus(Constant.OrderStatusEnum.FINISHED.getCode());
            order.setEndTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);
        } else {
            //若订单没有支付，以及其他情况
            throw new AlphenMallException(AlphenMallExceptionEnum.WRONG_STATUS_ORDER);
        }
    }
}
