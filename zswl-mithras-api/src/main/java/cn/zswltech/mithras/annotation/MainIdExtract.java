package cn.zswltech.mithras.annotation;

import org.springframework.stereotype.Indexed;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 子表编辑的请求体里
 * 主表id的取值逻辑
 *
 * @author wangchuanhao
 * @date 2022/11/17 10:07 AM
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Indexed
public @interface MainIdExtract {

    /**
     * 取值逻辑
     * @return
     */
    String expression();

}
