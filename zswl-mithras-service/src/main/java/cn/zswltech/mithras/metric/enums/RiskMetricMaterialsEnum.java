package cn.zswltech.mithras.metric.enums;

import cn.zswltech.mithras.service.config.enumscan.IMaterialsTypeConvert;
import cn.zswltech.mithras.common.enums.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author dingqi
 * @date 2023/1/3
 * @description
 */
@AllArgsConstructor
@Getter
public enum RiskMetricMaterialsEnum implements PullDown, IMaterialsTypeConvert {
    REPORT_FILE("财务相关报告");

    private final String display;

    @Override
    public String businessModule() {
        return "RISK_METRIC";
    }

    @Override
    public String display() {
        return this.display;
    }
}
