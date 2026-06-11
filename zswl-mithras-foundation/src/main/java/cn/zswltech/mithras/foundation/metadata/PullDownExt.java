package cn.zswltech.mithras.foundation.metadata;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * selectAll接口pulldown名称
 *
 * @author wangchuanhao
 * @date 2023/1/16 2:26 PM
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PullDownExt {
    String value();
}
