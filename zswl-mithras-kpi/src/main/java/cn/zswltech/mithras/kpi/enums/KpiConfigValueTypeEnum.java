package cn.zswltech.mithras.kpi.enums;

import cn.zswltech.mithras.foundation.metadata.PullDown;
import lombok.AllArgsConstructor;

/**
 * @author dingqi
 * @date 2023/2/10
 * @description
 */
@AllArgsConstructor
public enum KpiConfigValueTypeEnum implements PullDown {
    // 值
    VALUE("值"),
    // 公式
    FORMULA("公式");

    private final String display;

    @Override
    public String display() {
        return this.display;
    }
}
