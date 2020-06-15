package com.yxtech.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by yanfei on 2015/12/9.
 */
public class CORSFilter extends OncePerRequestFilter {
    private final static Logger log = LoggerFactory.getLogger(CORSFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String referer = request.getHeader("Origin");
        if(StringUtils.isEmpty(referer)){
            response.addHeader("Access-Control-Allow-Origin", "*");
        }else {
//            referer = referer.substring("http://".length());
//            referer = referer.substring(0,referer.indexOf("/"));
            response.addHeader("Access-Control-Allow-Origin", referer);
        }
        response.addHeader("Access-Control-Allow-Credentials","true");
        response.addHeader("Access-Control-Allow-Headers", "X-Requested-With,X_Requested_With,auth,content-type");
        if(request.getHeader("Access-Control-Request-Method") != null && "OPTIONS".equals(request.getMethod())) {
            log.debug("a options request.");
            response.addHeader("Access-Control-Allow-Methods", "HEAD,GET,POST,PUT,DELETE,OPTIONS");
            response.addHeader("Access-Control-Allow-Headers", "Content-Type,Origin,Accept");
            response.addHeader("Access-Control-Max-Age", "120");

            //options请求直接放行
            response.setStatus(202);
            return;
        }

        filterChain.doFilter(request, response);
    }
}