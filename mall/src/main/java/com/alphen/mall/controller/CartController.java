package com.alphen.mall.controller;

import com.alphen.mall.common.ApiRestResponse;
import com.alphen.mall.common.Constant;
import com.alphen.mall.filter.UserFilter;
import com.alphen.mall.service.CartService;
import com.alphen.mall.vo.CartVO;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
* 购物车控制器
* */
@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    /*
    * 购物车中添加商品,不在购物车页面操作
    * */
    @PostMapping("/add")
    public ApiRestResponse add(@RequestParam Integer productId,@RequestParam Integer count){
        cartService.add(UserFilter.currentUser.getId(),productId,count);
        return ApiRestResponse.success();
    }

    /*
    * 购物车列表
    * */
    @GetMapping("/list")
    public ApiRestResponse list(){
        //内部获取id,防止横向越权
        List<CartVO> cartVOList = cartService.list(UserFilter.currentUser.getId());
        return ApiRestResponse.success(cartVOList);
    }

    /*
    * 更新购物车信息
    * */
    @PostMapping("/update")
    public ApiRestResponse update(@RequestParam Integer productId,@RequestParam Integer count){
        List<CartVO> cartVOList = cartService.update(UserFilter.currentUser.getId(),productId,count);
        return ApiRestResponse.success(cartVOList);
    }

    /*
    * 删除购物车信息
    * */
    @PostMapping("/delete")
    public ApiRestResponse delete(@RequestParam Integer productId){
        List<CartVO> cartVOList = cartService.delete(UserFilter.currentUser.getId(),productId);
        return ApiRestResponse.success(cartVOList);
    }

    /*
    * 选中/不选中某商品
    * */
    @PostMapping("/select")
    public ApiRestResponse select(@RequestParam Integer productId,@RequestParam Integer selected){
        List<CartVO> cartVOList = cartService.selectOrNot(UserFilter.currentUser.getId(),productId,selected);
        return ApiRestResponse.success(cartVOList);
    }

    /*
     * 全选中/不全选中某商品
     * */
    @PostMapping("/selectAll")
    public ApiRestResponse selectAll(@RequestParam Integer selected){
        List<CartVO> cartVOList = cartService.selectAllOrNot(UserFilter.currentUser.getId(),selected);
        return ApiRestResponse.success(cartVOList);
    }

}
