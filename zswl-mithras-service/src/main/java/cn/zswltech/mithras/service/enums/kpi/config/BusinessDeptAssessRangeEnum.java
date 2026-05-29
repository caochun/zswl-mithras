package cn.zswltech.mithras.service.enums.kpi.config;

import cn.zswltech.mithras.common.enums.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author dingqi
 * @date 2023/2/13
 * @description 业务部门综合考评系数
 */
@Getter
@AllArgsConstructor
public enum BusinessDeptAssessRangeEnum implements PullDown {
    RANGE1("[100,null)", "100分（含）以上"),
    RANGE2("[95,100)", "95分（含）-100分"),
    RANGE3("[90,95)", "90分（含）-95分"),
    RANGE4("[60,95)", "60分（含）-90分"),
    RANGE5("[50,60)", "50分（含）-60分"),
    RANGE6("(null,50)", "50分以下");

    private final String formula;
    private final String display;

    @Override
    public String display() {
        return this.display;
    }
}
