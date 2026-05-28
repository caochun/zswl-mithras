package cn.zswltech.mithras.service.config;

import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Order(value= Ordered.HIGHEST_PRECEDENCE)
@Component
@Slf4j
public class LogErrorHandlerExceptionResolver extends DefaultErrorAttributes {

    private static final String ERROR_ATTRIBUTE = DefaultErrorAttributes.class.getName() + ".ERROR";

    @Override
    public ModelAndView resolveException(HttpServletRequest request,
                                         HttpServletResponse response,
                                         Object handler, Exception ex) {
        // 仅打个日志 不做处理 用于记录下操作失败的用户和接口
        // 该处处理的应该是还没到Controller的框架报错
        // 返回值的处理方式完全与 DefaultErrorAttributes 一致
        boolean recordErrorLogFlag = true;
        if (handler instanceof HandlerMethod) {
            HandlerMethod h = (HandlerMethod) handler;
            if (h.toString().contains("cn.zswltech.mithras") && h.toString().contains("Controller")) {
                // controller 抛出的异常会被 GlobalExceptionHandler 处理 此处不需要再记录日志
                recordErrorLogFlag = false;
            }
        }
        if (recordErrorLogFlag) {
            log.error("unControllerException, loginUserId:{}, uri:{}",
                    Optional.ofNullable(AccountUtil.getLoginInfo()).map(AccountVO::getId).orElse(null),
                    request.getRequestURI() + (StringUtils.isBlank(request.getQueryString()) ? "" : "?" + request.getQueryString()),
                    ex
            );
        }
        request.setAttribute(ERROR_ATTRIBUTE, ex);
        return null;
    }

}