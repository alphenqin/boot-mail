package com.alphen.mall.filter;

import com.alphen.mall.common.Constant;
import com.alphen.mall.model.pojo.User;
import com.alphen.mall.service.CategoryService;
import com.alphen.mall.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

/*
* 用户身份校验过滤器
* */
public class UserFilter implements Filter {

    //通过session保存的前端传来的对象，进行永久的保存
    public static User currentUser;

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
        currentUser = (User) session.getAttribute(Constant.ALPHEN_MALL_USER);
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
        filterChain.doFilter(servletRequest,servletResponse);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
