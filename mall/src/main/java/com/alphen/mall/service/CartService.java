package com.alphen.mall.service;

import com.alphen.mall.vo.CartVO;

import java.util.List;

public interface CartService {

    /*
    * 向购物车添加商品
    * */
    List<CartVO> add(Integer userId, Integer productId, Integer count);

    /*
    * 购物车列表展示
    * */
    List<CartVO> list(Integer userId);

    /*
    * 更新购物车
    * */
    List<CartVO> update(Integer userId, Integer productId, Integer count);

    /*
    * 删除该购物车信息
    * */
    List<CartVO> delete(Integer userId, Integer productId);

    /*
     * 选中/未选中某商品
     * */
    List<CartVO> selectOrNot(Integer userId, Integer productId, Integer selected);

    /*
    * 全选/全不选
    * */
    List<CartVO> selectAllOrNot(Integer userId, Integer selected);
}
