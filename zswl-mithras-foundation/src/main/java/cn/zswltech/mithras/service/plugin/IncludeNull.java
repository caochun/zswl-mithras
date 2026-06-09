package cn.zswltech.mithras.service.plugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author dingqi
 * @date 2022/10/20
 * @description 使用 {@link CustomBaseMapper#updateAnnotationIncludeNullById} 更新对象时，如果需要属性更新为null值的，需在对应属性上添加该注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface IncludeNull {
}
