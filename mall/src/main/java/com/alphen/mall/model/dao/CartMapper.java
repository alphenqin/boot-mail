package com.alphen.mall.model.dao;

import com.alphen.mall.model.pojo.Cart;
import com.alphen.mall.vo.CartVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Cart record);

    int insertSelective(Cart record);

    Cart selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);

    //根据用户id和商品id查询购物车实体
    Cart selectByUserIdAndProductId(@Param("userId") Integer userId, @Param("productId") Integer productId);

    //根据用户id查询需要展现给前端的CartVO实体
    List<CartVO> selectList(@Param("userId") Integer userId);

    //选中商品后调整为是否选中
    int selectOrNot(@Param("userId") Integer userId,@Param("productId") Integer productId,@Param("selected") Integer selected);
}