package cn.zswltech.mithras.foundation.annotation;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于临时区和最新版本数据比较，如无变动则不允许修改
 * 对比是否有差异时
 *
 * @author wangchuanhao
 * @date 2022/7/18 11:32 AM
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface NotCompareColumn {

    IgnoreLevel ignoreLevel() default IgnoreLevel.IGNORE;

    @AllArgsConstructor
    @Getter
    enum IgnoreLevel {

        /**
         * 完全不需要比较
         */
        IGNORE,

        /**
         * 需要比较，但如果有差异 不需要审批
         */
        NOT_APPROVAL,
        ;

    }

}
