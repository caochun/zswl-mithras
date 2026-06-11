package cn.zswltech.mithras.system.infrastructure.config;

import cn.hutool.core.util.ObjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@WebFilter(filterName = "CorsFilter")
@Configuration
public class CorsFilter implements Filter {

    @Autowired
    private CrosOriginWhiteListProperties corsWhiteList;

    //private static final List<String> ALLOW_ORIGINS = Arrays.asList("192\\..*", "127.0.0.1");
    private static final List<Pattern> ALLOW_ORIGINS_PATTERN = Arrays.asList(Pattern.compile("https?://192.168\\..*"), Pattern.compile("https?://127.0.0.1.*"));

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;
        // 测试阶段允许所有域名访问 不能设* 前端会报错
        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
//        if (hostAllow(request.getHeader("Origin"))) {
//            response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
//        }
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "OPTIONS, POST, GET, PATCH, DELETE, PUT");
        response.setHeader("Access-Control-Max-Age", "86400");
        response.setHeader("Access-Control-Allow-Headers", "*");
        chain.doFilter(req, res);
    }

    private boolean hostAllow(String origin) {
        if (ObjectUtil.isEmpty(corsWhiteList.getCorsWhiteList())) {
            return true;
        } else {
            return corsWhiteList.getCorsWhiteList().contains(origin);
        }
    }
}
