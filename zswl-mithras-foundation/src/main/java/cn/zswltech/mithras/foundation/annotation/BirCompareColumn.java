package cn.zswltech.mithras.foundation.annotation;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Description: 用户编辑工商信息 不同请款必填对比
 * 排序规则就是 校验最轻松 排序月小   跳过最为优先  如果是全量校验-特殊处理
 * @Author: huangping
 * @Date: 2025/11/26  9:59
 * @Version: 1.0
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface BirCompareColumn {

    BirCompareColumn.CompareLv comparLv() default BirCompareColumn.CompareLv.SKIP;

    @AllArgsConstructor
    @Getter
    enum CompareLv {

        SKIP("跳过",0),

        ABROAD("境外客户",30),

        DOMESTIC("境内客户",50),

        //境内集团客户
        DOMESTIC_UNGROUP("境内+非集团客户",100),

        ALL("全量校验",999),
        ;

        public final String display;

        public final Integer sort;
    }

}
