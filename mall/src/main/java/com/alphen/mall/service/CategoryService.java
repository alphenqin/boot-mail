package com.alphen.mall.service;

import com.alphen.mall.model.pojo.Category;
import com.alphen.mall.request.AddCategoryReq;
import com.alphen.mall.vo.CategoryVO;
import com.github.pagehelper.PageInfo;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

public interface CategoryService {
    /*
    * 后台-增加目录
    * */
    void add(AddCategoryReq addCategoryReq);

    /*
    * 更新目录
    * */
    void update(Category updateCategory);

    /*
    * 删除目录
    * */
    void delete(Integer id);

    /*
    * 目录分页功能
    * */
    PageInfo listForAdmin(Integer pageNum, Integer pageSize);

    /*
    * 前台递归展现目录
    * */
    @Cacheable(value = "listCategoryForCustomer")
    List<CategoryVO> listCategoryForCustomer(Integer parentId);

    void recursivelyFindCategories(List<CategoryVO> categoryVOList, Integer parentId);
}
