package cn.zswltech.mithras.service.auth.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 管理员权限校验
 *
 * @author wangchuanhao
 * @date 2022/12/6 1:45 PM
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AdminAuthCheck {
}
