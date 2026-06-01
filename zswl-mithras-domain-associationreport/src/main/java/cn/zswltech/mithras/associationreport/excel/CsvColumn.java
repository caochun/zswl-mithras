package cn.zswltech.mithras.associationreport.excel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// 自定义注解用于标记CSV列
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface CsvColumn {
        /**
         * 列显示名称（默认使用字段名）
         */
        String name() default "";

        /**
         * 列顺序（越小越靠前）
         */
        int order() default Integer.MAX_VALUE;

        /**
         * 是否忽略该字段
         */
        boolean ignored() default false;
    }