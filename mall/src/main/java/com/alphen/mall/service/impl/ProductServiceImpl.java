package com.alphen.mall.service.impl;

import com.alphen.mall.common.Constant;
import com.alphen.mall.exception.AlphenMallException;
import com.alphen.mall.exception.AlphenMallExceptionEnum;
import com.alphen.mall.model.dao.ProductMapper;
import com.alphen.mall.model.pojo.Product;
import com.alphen.mall.query.ProductListQuery;
import com.alphen.mall.request.AddProductReq;
import com.alphen.mall.request.ProductListReq;
import com.alphen.mall.service.CategoryService;
import com.alphen.mall.service.ProductService;
import com.alphen.mall.vo.CategoryVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryService categoryService;
    /*
    * 后台-添加商品信息
    * */
    @Override
    public void add(AddProductReq addProductReq){
        Product productOld = productMapper.selectByName(addProductReq.getName());
        if(productOld !=null){
            throw new AlphenMallException(AlphenMallExceptionEnum.NAME_EXISTED);
        }
        Product product = new Product();
        BeanUtils.copyProperties(addProductReq,product);
        int count = productMapper.insertSelective(product);
        if(count == 0){
            throw new AlphenMallException(AlphenMallExceptionEnum.CREATE_FAILED);
        }
    }

    /*
    * 后台-更新商品信息
    * */
    @Override
    public void update(Product updateProduct){
        Product productOld = productMapper.selectByName(updateProduct.getName());
        //当名字信息一样以及id不同
        if(productOld != null && !productOld.getId().equals(updateProduct.getId())){
            throw new AlphenMallException(AlphenMallExceptionEnum.UPDATE_FAILED);
        }
        int count = productMapper.updateByPrimaryKey(updateProduct);
        if(count == 0){
            throw new AlphenMallException(AlphenMallExceptionEnum.UPDATE_FAILED);
        }
    }

    /*
    * 后台-删除商品信息
    * */
    @Override
    public void delete(Integer id){
        Product product = productMapper.selectByPrimaryKey(id);
        if(product == null){
            throw new AlphenMallException(AlphenMallExceptionEnum.DELETE_FAILED);
        }
        int count = productMapper.deleteByPrimaryKey(id);
        if(count == 0){
            throw new AlphenMallException(AlphenMallExceptionEnum.DELETE_FAILED);
        }
    }

    /*
    * 后台-批量上下架商品
    * */
    @Override
    public void batchUpdateSellStatus(Integer[] ids, Integer sellStatus){
        productMapper.batchUpdateSellStatus(ids,sellStatus);
    }

    /*
    * 后台商品信息列表分页
    * */
    @Override
    public PageInfo listForAdmin(Integer pageNum, Integer pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<Product> productList = productMapper.selectListForAdmin();
        PageInfo pageInfo = new PageInfo(productList);
        return pageInfo;
    }

    /*
    * 前台单个商品详情
    * */
    @Override
    public Product detail(Integer id){
        Product product = productMapper.selectByPrimaryKey(id);
        return product;
    }

    /*
    * 前台搜索后商品信息分页
    * */
    @Override
    public PageInfo list(ProductListReq productListReq){
        ProductListQuery productListQuery = new ProductListQuery();
        //搜索处理，假如搜索框不为空
        if(!StringUtils.isEmpty(productListReq.getKeyword())){
            String keyword = new StringBuilder().append("%").append(productListReq.getKeyword()).append("%").toString();
            //放入查询实体，用于数据库查询
            productListQuery.setKeyword(keyword);
        }
        //目录处理，如果查询某个目录下的商品，那么必须也要查到该目录下所有子目录下的商品，故要得到一个目录id集合
        //若请求实体传过来目录id
        if(productListReq.getCategoryId() != null){
            //获取目录ID树状结构
            List<CategoryVO> categoryVOList = categoryService.listCategoryForCustomer(productListReq.getCategoryId());
            List<Integer> categoryIds = new ArrayList<>();
            categoryIds.add(productListReq.getCategoryId());
            getCategoryIds(categoryVOList,categoryIds);
            productListQuery.setCategoryIds(categoryIds);
        }
        //排序处理
        String orderBy = productListReq.getOrderBy();
        if(Constant.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)){
            PageHelper.startPage(productListReq.getPageNum(),productListReq.getPageSize(),orderBy);
        }else {
            PageHelper.startPage(productListReq.getPageNum(),productListReq.getPageSize());
        }
        List<Product> productList = productMapper.selectListForCustomer(productListQuery);
        PageInfo pageInfo = new PageInfo(productList);
        return pageInfo;
    }
    //获取所有目录的id
    public void getCategoryIds(List<CategoryVO> categoryVOList,List<Integer> categoryIds){
        for(int i = 0;i<categoryVOList.size();i++){
            CategoryVO categoryVO = categoryVOList.get(i);
            if(categoryVO != null){
                categoryIds.add(categoryVO.getId());
                getCategoryIds(categoryVO.getChildCategoryList(),categoryIds);
            }
        }
    }
}
