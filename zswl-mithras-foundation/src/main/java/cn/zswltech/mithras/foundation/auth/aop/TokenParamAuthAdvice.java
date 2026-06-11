package cn.zswltech.mithras.foundation.auth.aop;


import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.gruul.common.constant.SystemConstant;
import cn.zswltech.gruul.common.result.MSG;
import cn.zswltech.gruul.common.util.CookieUtils;
import cn.zswltech.gruul.common.util.JwtUtils;
import cn.zswltech.gruul.domain.entity.CheckResult;
import cn.zswltech.mithras.foundation.auth.aop.TokenParamAuth;
import cn.zswltech.mithras.foundation.auth.checker.AuthHelper;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
@Slf4j
public class TokenParamAuthAdvice {

    @Resource
    private AuthHelper authHelper;

    @Pointcut("@annotation(cn.zswltech.mithras.foundation.auth.aop.TokenParamAuth)")
    public void authCheck() {
    }

    @Before("authCheck() && @annotation(tokenParamAuth)")
    public void doBefore(JoinPoint joinPoint, TokenParamAuth tokenParamAuth) {
        Object[] args = joinPoint.getArgs();
        Object arg = args[tokenParamAuth.paramIndex()];
        Object tokenObject =  authHelper.extractObjectByExpression(arg, tokenParamAuth.keyFieldName());
        if(ObjectUtil.isNull(tokenObject)){
            throw new MithrasException("token为空");
        }
        String token;
        try {
            token = JSONObject.parseObject(tokenObject.toString()).getString(CookieUtils.WL_TOKEN);
        }catch (Exception e){
            throw new MithrasException("token数据格式不合法");
        }
        if(ObjectUtil.isNull(token)){
            throw new MithrasException("token信息丢失");
        }
        //权限校验
        CheckResult checkResult = JwtUtils.validateJWT(token);
        if (!checkResult.isSuccess()) {
            switch (checkResult.getErrCode()) {
                // 签名验证不通过
                case SystemConstant.JWT_ERRCODE_FAIL:
                    log.info("签名验证不通过");
                    throw new MithrasException(MSG.req_error_sign_not_pass.getMsg());
                // 签名过期，返回过期提示码
                case SystemConstant.JWT_ERRCODE_EXPIRE:
                    log.info("签名过期");
                    throw new MithrasException(MSG.req_error_sign_expire.getMsg());
                default:
                    break;
            }
            throw new MithrasException(MSG.old_unknow_error.getMsg());
        }
        //设置用户信息
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null){
            return;
        }
        HttpServletRequest request = requestAttributes.getRequest();
        request.setAttribute(CookieUtils.WL_TOKEN, token);
    }



}
