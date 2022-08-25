package com.alphen.mall.service.impl;

import com.alphen.mall.common.Constant;
import com.alphen.mall.exception.AlphenMallException;
import com.alphen.mall.exception.AlphenMallExceptionEnum;
import com.alphen.mall.model.dao.CartMapper;
import com.alphen.mall.model.dao.ProductMapper;
import com.alphen.mall.model.pojo.Cart;
import com.alphen.mall.model.pojo.Product;
import com.alphen.mall.service.CartService;
import com.alphen.mall.vo.CartVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/*
* 购物车Service
* */
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    /*
     * 向购物车添加商品
     * */
    @Override
    public List<CartVO> add(Integer userId, Integer productId, Integer count) {
        validProduct(productId, count);
        Cart cart = cartMapper.selectByUserIdAndProductId(userId, productId);
        if (cart == null) {
            //这个商品之前不在购物车里
            cart = new Cart();
            cart.setProductId(productId);
            cart.setUserId(userId);
            cart.setQuantity(count);
            cart.setSelected(Constant.Cart.CHECKED);
            cartMapper.insertSelective(cart);
        } else {
            //这个商品之前本来就存在
            count = cart.getQuantity() + count;
            cart.setQuantity(count);
            cart.setSelected(Constant.Cart.CHECKED);
            cartMapper.updateByPrimaryKeySelective(cart);
        }
        return this.list(userId);
    }

    //判断该商品是否有效
    public void validProduct(Integer productId, Integer count) {
        Product product = productMapper.selectByPrimaryKey(productId);
        //判断商品是否存在以及是否在上架中
        if (product == null || product.getStatus().equals(Constant.SaleStatus.NOT_SALE)) {
            throw new AlphenMallException(AlphenMallExceptionEnum.NOT_SALE);
        }
        //判断商品库存
        if (product.getStock() < count) {
            throw new AlphenMallException(AlphenMallExceptionEnum.NOT_ENOUGH);
        }
    }

    /*
     * 购物车列表展示
     * */
    @Override
    public List<CartVO> list(Integer userId) {
        List<CartVO> cartVOList = cartMapper.selectList(userId);
        for (int i = 0; i < cartVOList.size(); i++) {
            CartVO cartVO = cartVOList.get(i);
            cartVO.setTotalPrice(cartVO.getQuantity() * cartVO.getPrice());
        }
        return cartVOList;
    }

    /*
     * 更新购物车
     * */
    @Override
    public List<CartVO> update(Integer userId, Integer productId, Integer count) {
        validProduct(productId, count);
        Cart cart = cartMapper.selectByUserIdAndProductId(userId, productId);
        if (cart == null) {
            //这个商品之前不在购物车里,则无法更新
            throw new AlphenMallException(AlphenMallExceptionEnum.UPDATE_FAILED);
        } else {
            //这个商品之前本来就存在，则更新数量
            cart.setProductId(cart.getProductId());
            cart.setUserId(cart.getUserId());
            cart.setQuantity(count);
            cart.setSelected(Constant.Cart.CHECKED);
            cartMapper.updateByPrimaryKeySelective(cart);
        }
        return this.list(userId);
    }

    /*
     * 删除该购物车信息
     * */
    @Override
    public List<CartVO> delete(Integer userId, Integer productId) {
        Cart cart = cartMapper.selectByUserIdAndProductId(userId, productId);
        if (cart == null) {
            //这个商品之前不在购物车里,则无法删除
            throw new AlphenMallException(AlphenMallExceptionEnum.DELETE_FAILED);
        } else {
            //这个商品之前本来就存在，则直接删除
            cartMapper.deleteByPrimaryKey(cart.getId());
        }
        return this.list(userId);
    }

    /*
     * 选中/未选中某商品
     * */
    @Override
    public List<CartVO> selectOrNot(Integer userId, Integer productId, Integer selected) {
        Cart cart = cartMapper.selectByUserIdAndProductId(userId, productId);
        if (cart == null) {
            //这个商品不在购物车里,则无法选中
            throw new AlphenMallException(AlphenMallExceptionEnum.UPDATE_FAILED);
        } else {
            //这个商品本来就存在，则选中
            cartMapper.selectOrNot(userId, productId, selected);
        }
        return this.list(userId);
    }

    /*
    * 全选/全不选
    * */
    @Override
    public List<CartVO> selectAllOrNot(Integer userId,Integer selected){
        //改变该用户在购物车所有商品的选中状态
        cartMapper.selectOrNot(userId,null,selected);
        return this.list(userId);
    }
}