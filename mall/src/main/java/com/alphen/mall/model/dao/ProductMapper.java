package com.alphen.mall.model.dao;

import com.alphen.mall.model.pojo.Product;
import com.alphen.mall.query.ProductListQuery;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Product record);

    int insertSelective(Product record);

    Product selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Product record);

    int updateByPrimaryKey(Product record);
    //根据商品名字查询商品信息实体
    Product selectByName(String name);
    //批量改变指定集合id下的商品的上下架状态
    Integer batchUpdateSellStatus(@Param("ids") Integer[] ids,@Param("sellStatus") Integer sellStatus);
    //后台获取所有商品信息
    List<Product> selectListForAdmin();
    //前台根据查询条件获取商品信息
    List<Product> selectListForCustomer(@Param("query") ProductListQuery query);
}