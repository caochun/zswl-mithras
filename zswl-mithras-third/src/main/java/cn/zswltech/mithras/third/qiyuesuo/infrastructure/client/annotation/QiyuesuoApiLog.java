package cn.zswltech.mithras.third.qiyuesuo.infrastructure.client.annotation;

import java.lang.annotation.*;

/**
 * @author bigbear
 * @date 2024/12/11 19:44
 * @description 契约锁日志记录切面
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface QiyuesuoApiLog {
}
