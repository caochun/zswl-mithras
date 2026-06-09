package cn.zswltech.mithras.service.auth.aop;

import cn.zswltech.mithras.service.auth.checker.IDataAuthChecker;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据权限校验
 *
 * @author wangchuanhao
 * @date 2022/7/21 1:49 PM
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DataAuthCheck {

    /**
     * 默认值可以没用
     * @return
     */
    Class<? extends BaseMapper>[] mapperClass() default {BaseMapper.class};

    /**
     * 关键字段名
     * @return
     */
    String keyFieldName() default "";

    /**
     *
     * @return
     */
    Class<? extends IDataAuthChecker> checkerClass();

    /**
     * 参数取值类型
     * @return
     */
    ParamType paramType() default ParamType.OBJECT;

    /**
     * 业务模块
     * @return
     */
    String businessModule();

    /**
     * 参数下标
     * @return
     */
    int paramIndex() default 0;

    /**
     * 取值方式
     */
    enum ParamType {

        /**
         * 参数直取
         */
        DIRECT,

        /**
         * 对象中取字段
         */
        OBJECT,

        /**
         * 无需取值
         */
        NO
        ;

    }

}
