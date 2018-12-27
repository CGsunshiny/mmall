package com.mmall.filter;

import com.mmall.pojo.User;
import com.mmall.util.RedisUtil;
import com.mmall.util.TokenUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@WebFilter(filterName = "resetUserCache",urlPatterns = "/*")
public class ResetUserCache implements Filter {

    Logger logger = LoggerFactory.getLogger(ResetUserCache.class);

    @Autowired
    RedisUtil redisUtil;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

//        获取token
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        String token = TokenUtil.getTokenByRequest(request);
        if (StringUtils.isNotBlank(token)){
            Object obj = redisUtil.get(token);
            User user = null;
            if (obj instanceof User){
                 user = (User)obj;
            }
            if (user!=null){
                redisUtil.set(token,user,60*30);
                logger.info("重置了用户登录信息到redis中");
            }
        }
        filterChain.doFilter(servletRequest,servletResponse);
    }
    @Override
    public void destroy() {

    }
}
