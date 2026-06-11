package cn.zswltech.mithras.foundation.auth.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 *
 * @author: 三方调用接口校验合法性
 * @date: 2022/10/17 4:13 下午
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ThirdAuthCheck {
}
