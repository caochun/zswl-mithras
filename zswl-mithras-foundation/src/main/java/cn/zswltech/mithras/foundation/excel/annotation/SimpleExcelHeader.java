package cn.zswltech.mithras.foundation.excel.annotation;

import cn.zswltech.mithras.foundation.excel.ColumnStyleEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author dingqi
 * @date 2023/3/20
 * @description
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SimpleExcelHeader {
    String headerName();

    int headerOrder() default -1;

    ColumnStyleEnum columnStyle() default ColumnStyleEnum.DEFAULT;
}
