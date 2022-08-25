package com.alphen.mall.controller;

import com.alphen.mall.common.ApiRestResponse;
import com.alphen.mall.common.Constant;
import com.alphen.mall.exception.AlphenMallException;
import com.alphen.mall.exception.AlphenMallExceptionEnum;
import com.alphen.mall.model.pojo.Product;
import com.alphen.mall.request.AddProductReq;
import com.alphen.mall.request.UpdateProductReq;
import com.alphen.mall.service.ProductService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.UUID;

/*
* 后台商品管理控制器
* */
@RestController//全部默认加@RequestBody
public class ProductAdminController {

    @Autowired
    private ProductService productService;

    /*
    * 后台-商品信息添加
    * */
    @PostMapping("/admin/product/add")
    public ApiRestResponse addProduct(@Valid @RequestBody AddProductReq addProductReq){
        //目前添加商品的时间
        addProductReq.setCreateTime(new Date());
        productService.add(addProductReq);
        return ApiRestResponse.success();
    }

    @PostMapping("/admin/upload/file")
    public ApiRestResponse upload(HttpServletRequest httpServletRequest, @RequestParam("file") MultipartFile file){
        //获取原始文件名的后缀
        String suffixName = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        //通过UUID生成随机文件名
        UUID uuid = UUID.randomUUID();
        String newFileName = uuid.toString()+suffixName;
        //创建文件夹
        File fileDirectory = new File(Constant.FILE_UPLOAD_DIR);
        //创建目标文件
        File destFile = new File(Constant.FILE_UPLOAD_DIR+newFileName);
        //判断文件是否存在
        if(!fileDirectory.exists()){
            //若建立文件夹失败
            if(!fileDirectory.mkdir()){
                throw new AlphenMallException(AlphenMallExceptionEnum.MKDIR_FAILED);
            }
        }
        try {
            //把请求里的文件写入到创建的目标文件
            file.transferTo(destFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            return ApiRestResponse.success(getHost(new URI(httpServletRequest.getRequestURL()+""))+
                    "/images/"+newFileName);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return ApiRestResponse.error(AlphenMallExceptionEnum.UPLOAD_FAILED);
        }
    }
//    获取自定义的URI
    public URI getHost(URI uri){
        URI effectiveURI;
        try {
            effectiveURI = new URI(uri.getScheme(),uri.getUserInfo(),uri.getHost(),uri.getPort(),null,null,null);
        } catch (URISyntaxException e) {
            effectiveURI = null;
        }
        return effectiveURI;
    }

    /*
    * 后台更新商品信息
    * */
    @PostMapping("/admin/product/update")
    public ApiRestResponse updateProduct(@Valid @RequestBody UpdateProductReq updateProductReq){
        Product product = new Product();
        //目前更新商品信息的时间
        updateProductReq.setUpdateTime(new Date());
        BeanUtils.copyProperties(updateProductReq,product);
        productService.update(product);
        return ApiRestResponse.success();
    }

    /*
    * 后台删除商品
    * */
    @PostMapping("/admin/product/delete")
    public ApiRestResponse deleteProduct(@RequestParam Integer id){
        productService.delete(id);
        return ApiRestResponse.success();
    }

    /*
    * 后台批量上下架商品
    * */
    @PostMapping("/admin/product/batchUpdateSellStatus")
    public ApiRestResponse batchUpdateSellStatus(@RequestParam Integer[] ids,@RequestParam Integer sellStatus){
        productService.batchUpdateSellStatus(ids,sellStatus);
        return ApiRestResponse.success();
    }

    /*
    * 后台商品信息列表
    * */
    @PostMapping("/admin/product/list")
    public ApiRestResponse listForAdmin(@RequestParam Integer pageNum,@RequestParam Integer pageSize){
        PageInfo pageInfo = productService.listForAdmin(pageNum,pageSize);
        return ApiRestResponse.success(pageInfo);
    }
}
