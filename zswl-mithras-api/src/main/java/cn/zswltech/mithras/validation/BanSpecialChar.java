package cn.zswltech.mithras.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 禁止特殊字符
 *
 * @author wangchuanhao
 * @date 2022/12/21 4:01 PM
 */
@Documented
@Constraint(validatedBy = { BanSpecialCharValidator.class })
@Target({ FIELD })
@Retention(RUNTIME)
public @interface BanSpecialChar {

    String message() default "字段特殊字符规则校验失败(1、不允许使用换行符；2、首尾不能有空格；3、不允许使用制表符)";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

}
