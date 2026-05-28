package cn.zswltech.mithras.api.serial;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author luyi
 */
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@JsonSerialize(using = AmountScaleSerializer.class)
@JsonDeserialize(using = AmountScaleDeserializer.class)
public @interface AmountScale {
    long unit() default 1L;
}
