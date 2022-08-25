package com.alphen.mall.controller;

import com.alphen.mall.common.ApiRestResponse;
import com.alphen.mall.common.Constant;
import com.alphen.mall.exception.AlphenMallExceptionEnum;
import com.alphen.mall.model.pojo.Category;
import com.alphen.mall.model.pojo.User;
import com.alphen.mall.request.AddCategoryReq;
import com.alphen.mall.request.UpdateCategoryReq;
import com.alphen.mall.service.CategoryService;
import com.alphen.mall.service.UserService;
import com.alphen.mall.vo.CategoryVO;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

/*
* 目录控制器
* */
@Controller
public class CategoryController {

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;

    /*
    * 后台-添加目录
    * */
    @PostMapping("/admin/category/add")
    @ResponseBody
    @ApiOperation("后台添加目录")
    public ApiRestResponse addCategory(HttpSession session, @Valid @RequestBody AddCategoryReq addCategoryReq){
//        if(addCategoryReq.getName() == null || addCategoryReq.getType() == null ||
//                addCategoryReq.getParentId() == null || addCategoryReq.getOrderNum() == null){
//            return ApiRestResponse.error(AlphenMallExceptionEnum.NAME_NOT_NULL);
//        }
        User currentUser = (User) session.getAttribute(Constant.ALPHEN_MALL_USER);
        //校验当前是否登录
        if(currentUser == null){
            return ApiRestResponse.error(AlphenMallExceptionEnum.NEED_LOGIN);
        }
        //校验当前用户是否为管理员
        if(userService.checkAdmin(currentUser)){
            //是管理员，则执行添加目录操作
            categoryService.add(addCategoryReq);
            return ApiRestResponse.success();
        }else {
            return ApiRestResponse.error(AlphenMallExceptionEnum.NEED_ADMIN);
        }
    }

    /*
    * 后台更新目录
    * */
    @PostMapping("/admin/category/update")
    @ResponseBody
    public ApiRestResponse update(@Valid @RequestBody UpdateCategoryReq updateCategoryReq,HttpSession session){
        User currentUser = (User) session.getAttribute(Constant.ALPHEN_MALL_USER);
        //校验当前是否登录
        if(currentUser == null){
            return ApiRestResponse.error(AlphenMallExceptionEnum.NEED_LOGIN);
        }
        //校验当前用户是否为管理员
        if(userService.checkAdmin(currentUser)){
            //是管理员，则执行添加目录操作
            Category category = new Category();
            BeanUtils.copyProperties(updateCategoryReq,category);
            categoryService.update(category);
            return ApiRestResponse.success();
        }else {
            return ApiRestResponse.error(AlphenMallExceptionEnum.NEED_ADMIN);
        }
    }

    /*
    * 后台删除目录
    * */
    @PostMapping("/admin/category/delete")
    @ResponseBody
    public ApiRestResponse deleteCategory(@RequestParam Integer id){
        categoryService.delete(id);
        return ApiRestResponse.success();
    }

    /*
    * 后台目录列表
    * */
    @PostMapping("/admin/category/list")
    @ResponseBody
    public ApiRestResponse listCategoryForAdmin(@RequestParam Integer pageNum, @RequestParam Integer pageSize){
        PageInfo pageInfo = categoryService.listForAdmin(pageNum,pageSize);
        return ApiRestResponse.success(pageInfo);
    }

    /*
     * 前台 目录列表
     * */
    @PostMapping("/category/list")
    @ResponseBody
    public ApiRestResponse listCategoryForCustomer(){
        List<CategoryVO> categoryVOList = categoryService.listCategoryForCustomer(0);
        return ApiRestResponse.success(categoryVOList);
    }
}
