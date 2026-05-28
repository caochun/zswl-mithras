package cn.zswltech.mithras.blackgray.annotation;

import java.lang.annotation.*;

@Inherited
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface SensitiveData {
    //加解密标识 true加密 false解密
    boolean encrypt() default false;
}