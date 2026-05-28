package cn.zswltech.mithras.service.mapper.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author yibin
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SponsorField {
    String value();

    String belongDeptField();

    String cosponsorField() default "projCosponsorUserIds";
}
