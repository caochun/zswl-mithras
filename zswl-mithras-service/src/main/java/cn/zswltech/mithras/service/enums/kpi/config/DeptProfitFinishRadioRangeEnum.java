package cn.zswltech.mithras.service.enums.kpi.config;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author dingqi
 * @date 2023/2/13
 * @description 部门利润完成率系数
 */
@Getter
@AllArgsConstructor
public enum DeptProfitFinishRadioRangeEnum implements PullDown {
    RANGE1("[100,null)", "100% <= P"),
    RANGE2("[90,100)", "90% <= P < 100%"),
    RANGE3("[80,90)", "80% <= P < 90%"),
    RANGE4("[70,80)", "70% <= P < 80%"),
    RANGE5("(null,70)", "P < 70%");

    private final String formula;
    private final String display;

    @Override
    public String display() {
        return this.display;
    }
}
