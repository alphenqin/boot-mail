package com.alphen.mall.service;

import com.alphen.mall.model.pojo.Product;
import com.alphen.mall.request.AddProductReq;
import com.alphen.mall.request.ProductListReq;
import com.alphen.mall.vo.CategoryVO;
import com.github.pagehelper.PageInfo;

import java.util.List;

/*
* 商品Service
* */
public interface ProductService {

    /*
    * 后台-添加商品信息
    * */
    void add(AddProductReq addProductReq);

    /*
    * 后台-更新商品信息
    * */
    void update(Product updateProduct);

    /*
    * 后台-删除商品信息
    * */
    void delete(Integer id);

    /*
    * 后台-批量上下架商品
    * */
    void batchUpdateSellStatus(Integer[] ids, Integer sellStatus);

    /*
    * 后台商品信息列表分页
    * */
    PageInfo listForAdmin(Integer pageNum, Integer pageSize);

    /*
    * 前台单个商品详情
    * */
    Product detail(Integer id);

    /*
    * 前台搜索后商品信息分页
    * */
    PageInfo list(ProductListReq productListReq);

}
