package com.alphen.mall.service.impl;

import com.alphen.mall.exception.AlphenMallException;
import com.alphen.mall.exception.AlphenMallExceptionEnum;
import com.alphen.mall.model.dao.CategoryMapper;
import com.alphen.mall.model.pojo.Category;
import com.alphen.mall.request.AddCategoryReq;
import com.alphen.mall.service.CategoryService;
import com.alphen.mall.vo.CategoryVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    /*
    * 后台-增加目录
    * */
    @Override
    public void add(AddCategoryReq addCategoryReq){
        Category category = new Category();
//        将请求的目录对象复制到category中
        BeanUtils.copyProperties(addCategoryReq,category);
        Category categoryOld = categoryMapper.selectByName(category.getName());
        //校验是否重名
        if(categoryOld != null){
            throw new AlphenMallException(AlphenMallExceptionEnum.NAME_EXISTED);
        }
        int count = categoryMapper.insertSelective(category);
        //插入失败时
        if(count == 0){
            throw new AlphenMallException(AlphenMallExceptionEnum.CREATE_FAILED);
        }
    }

    /*
    * 更新目录
    * */
    @Override
    public void update(Category updateCategory){
        if(updateCategory.getName() != null){
//            找到名字相同的分类
            Category categoryOld = categoryMapper.selectByName(updateCategory.getName());
//            当该目录存在以及id不相等时=重名时
            if(categoryOld != null && !categoryOld.getId().equals(updateCategory.getId())){
                throw new AlphenMallException(AlphenMallExceptionEnum.NAME_EXISTED);
            }
            int count = categoryMapper.updateByPrimaryKeySelective(updateCategory);
            if(count == 0){
                throw new AlphenMallException(AlphenMallExceptionEnum.UPDATE_FAILED);
            }
        }
    }

    /*
    * 删除目录
    * */
    @Override
    public void delete(Integer id){
        Category categoryOld = categoryMapper.selectByPrimaryKey(id);
        //当该目录不存在时
        if(categoryOld == null){
            throw new AlphenMallException(AlphenMallExceptionEnum.DELETE_FAILED);
        }
        int count = categoryMapper.deleteByPrimaryKey(id);
        if(count == 0){
            throw new AlphenMallException(AlphenMallExceptionEnum.DELETE_FAILED);
        }
    }

    /*
    * 后台目录分页功能
    * */
    @Override
    public PageInfo listForAdmin(Integer pageNum, Integer pageSize){
//        定义分页方式
        PageHelper.startPage(pageNum,pageSize,"type,order_num");
        List<Category> categoryList = categoryMapper.selectList();
        PageInfo pageInfo = new PageInfo(categoryList);
        return pageInfo;
    }

    /*
    * 前台递归展现目录
    * */
    @Override
    @Cacheable(value = "listCategoryForCustomer")
    public List<CategoryVO> listCategoryForCustomer(Integer parentId){
        List<CategoryVO> categoryVOList = new ArrayList<>();
        recursivelyFindCategories(categoryVOList,parentId);
        return categoryVOList;
    }
    @Override
    public void recursivelyFindCategories(List<CategoryVO> categoryVOList, Integer parentId){
        //递归查询所有子目录,并组合成为一个"目录树"
        List<Category> categoryList = categoryMapper.selectCategoriesByParentId(parentId);
        if(!CollectionUtils.isEmpty(categoryList)){
            for(int i = 0;i<categoryList.size();i++){
//                该父ID下的所有目录
                Category category = categoryList.get(i);
                CategoryVO categoryVO = new CategoryVO();
                BeanUtils.copyProperties(category,categoryVO);
                categoryVOList.add(categoryVO);
                recursivelyFindCategories(categoryVO.getChildCategoryList(),categoryVO.getId());
            }
        }
    }
}
