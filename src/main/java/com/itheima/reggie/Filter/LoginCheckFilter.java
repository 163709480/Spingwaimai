package com.itheima.reggie.Filter;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 检查用户是否已经完成登录
 */


@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter  implements Filter {
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    //路径匹配器，支持通配符

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
    HttpServletRequest request =(HttpServletRequest)servletRequest;
    HttpServletResponse response=(HttpServletResponse) servletResponse;
        String requestURI = request.getRequestURI();
        log.info("拦截到请求 {}",requestURI);

        //定义不需要处理的请求路径
        String[] urls= new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",
                "/user/login",
                "/doc.html",
                "/webjars/**",
                "/swagger-resources",
                "/v2/api-docs"

        };
        boolean check = check(urls, requestURI);
//        2.判断本次请求是否需要处理
       if(check){
           log.info("不需要处理");
           filterChain. doFilter(request,response);
           //不需要处理
           return;
       }
       if(request.getSession().getAttribute("employee")!=null){
           log.info("用户的id为",request.getSession().getAttribute("employee"));


           Long employee = (Long) request.getSession().getAttribute("employee");
           BaseContext.setCruuentID(employee);





           log.info("线程id为: {}",employee);


           filterChain.doFilter(request,response);
           return;
       }
       //手机页面
        if(request.getSession().getAttribute("user")!=null){
            log.info("手机页面端的id为",request.getSession().getAttribute("user"));
            Long user =(Long) request.getSession().getAttribute("user");
            log.info("user的id值为 ={}",user);
            BaseContext.setCruuentID(user);

            filterChain.doFilter(request,response);
            return;
        }









           response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
           return;



    }

    /**
     * 路径匹配，检查本次请求是否需要放行
     * @param requestURl
     * @return
     */
    public boolean check(String[] urls,String requestURl){
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURl);
            if(match){
                return true;
            }
        }
        return false;
    }
}
