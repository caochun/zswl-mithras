package cn.zswltech.mithras.service.config;

import cn.zswltech.gruul.web.api.intercept.WhiteListUtil;
import cn.zswltech.mithras.service.util.ApprovalTestUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
 * 审批流测试
 *
 * @author wangchuanhao
 * @date 2022/8/9 12:35 PM
 */
@Component
public class ApprovalTestInterceptor implements HandlerInterceptor, WebMvcConfigurer {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        if (Boolean.TRUE.toString().equals(request.getHeader("ApprovalFlag"))) {
//            ApprovalTestUtil.setApprovalFlag(true);
//        }
        // 放开限制 直接提交审批
        ApprovalTestUtil.setApprovalFlag(true);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        ApprovalTestUtil.reset();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        String[] patterns = new String[]{"/flow/task/myProcess/count", "/proj/establish/effect", "/client/effect",
                "/proj/review/effect","/payment/effect","/after/lease/adjust/effect"};
        registry.addInterceptor(this).addPathPatterns(patterns);
    }

}