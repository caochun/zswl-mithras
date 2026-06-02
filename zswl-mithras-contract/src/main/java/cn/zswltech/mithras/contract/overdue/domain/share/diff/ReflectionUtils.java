package cn.zswltech.mithras.contract.overdue.domain.share.diff;

import cn.hutool.core.util.ReflectUtil;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/10/14 16:11
 */
public class ReflectionUtils {
    public static void writeField(String name, Object aggregate, Object fieldValue) {
        ReflectUtil.setFieldValue(aggregate, name, fieldValue);
    }
}
