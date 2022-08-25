package com.alphen.mall.filter;

import com.alphen.mall.common.Constant;
import com.alphen.mall.model.pojo.User;
import com.alphen.mall.service.CategoryService;
import com.alphen.mall.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;

/*
* 管理员身份校验过滤器
* */
public class AdminFilter implements Filter {

    @Autowired
    private UserService userService;
    @Autowired
    private CategoryService categoryService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
//        获取session
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute(Constant.ALPHEN_MALL_USER);
        //校验当前是否登录-没有登录时
        if(currentUser == null){
            PrintWriter out = new HttpServletResponseWrapper((HttpServletResponse)servletResponse).getWriter();
            out.write("{\n" +
                    "    \"status\": 10007,\n" +
                    "    \"msg\": \"NEED_LOGIN\",\n" +
                    "    \"data\": null\n" +
                    "}");
            out.flush();
            out.close();
            return;
        }
        //校验当前用户是否为管理员
        if(userService.checkAdmin(currentUser)){
            //是管理员，则执行添加目录操作
            filterChain.doFilter(servletRequest,servletResponse);
        }else {
            PrintWriter out = new HttpServletResponseWrapper((HttpServletResponse)servletResponse).getWriter();
            out.write("{\n" +
                    "    \"status\": 10009,\n" +
                    "    \"msg\": \"NEED_ADMIN\",\n" +
                    "    \"data\": null\n" +
                    "}");
            out.flush();
            out.close();
            return;
        }
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
