package cn.zswltech.mithras.service.auth.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于前端head无法携带tocken时，token以param形式传递验证
 * 注意需将路径加在配置文件gruul:system:intercept:excludepath:后，绕过统一认证
 * @author: jackerhe 
 * @date: 2023/2/8 10:29 上午
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface TokenParamAuth {

    /**
     * 关键字段名
     * @return
     */
    String keyFieldName() default "token";

    /**
     * 参数下标
     * @return
     */
    int paramIndex() default 0;
}
