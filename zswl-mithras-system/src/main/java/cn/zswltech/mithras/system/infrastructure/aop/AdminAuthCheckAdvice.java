package cn.zswltech.mithras.system.infrastructure.aop;

import cn.zswltech.mithras.foundation.annotation.AdminAuthCheck;
import cn.zswltech.mithras.foundation.exception.AuthCheckException;
import cn.zswltech.mithras.system.user.SysUserService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 管理员权限校验拦截
 *
 * @author wangchuanhao
 * @date 2022/7/21 2:40 PM
 */
@Aspect
@Component
public class AdminAuthCheckAdvice {

    @Resource
    private SysUserService sysUserService;

    @Pointcut("@annotation(cn.zswltech.mithras.foundation.annotation.AdminAuthCheck)")
    public void authCheck() {

    }

    @Before("authCheck() && @annotation(adminAuthCheck)")
    public void doBefore(JoinPoint joinPoint, AdminAuthCheck adminAuthCheck) {
        if (!sysUserService.adminAuth()) {
            // 非管理员无权操作
            throw new AuthCheckException("非管理员无权操作");
        }
    }

}
