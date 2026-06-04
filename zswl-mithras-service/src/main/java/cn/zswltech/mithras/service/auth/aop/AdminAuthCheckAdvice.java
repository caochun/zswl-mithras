package cn.zswltech.mithras.service.auth.aop;

import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.service.others.AuthCheckException;
import cn.zswltech.mithras.system.service.SysUserService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

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

    @Pointcut("@annotation(cn.zswltech.mithras.service.auth.aop.AdminAuthCheck)")
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
