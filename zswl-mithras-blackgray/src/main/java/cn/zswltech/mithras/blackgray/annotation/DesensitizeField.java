package cn.zswltech.mithras.blackgray.annotation;

import java.lang.annotation.*;

/**
 * 标记需要脱敏字段，目前仅支持字符串自定义
 * 需调用 BlackDesensitizeUtil.desensitize()
 **/
@Inherited
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface DesensitizeField {
    int ignorePrefix() default 0; //前x位不脱敏
    int ignoreSuffix() default 0; //前x位不脱敏 前后有重叠是，只保留前x位，后x位脱敏
}