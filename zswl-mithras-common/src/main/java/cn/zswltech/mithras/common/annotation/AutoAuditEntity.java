package cn.zswltech.mithras.common.annotation;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 自动记录操作用户的打标
 *
 * @author wangchuanhao
 * @date 2022/6/27 5:23 PM
 */
@Target({TYPE})
@Retention(RUNTIME)
@Inherited
public @interface AutoAuditEntity {

    boolean audit() default true;

}
