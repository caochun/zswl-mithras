package cn.zswltech.mithras.kpi.enums.config;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author dingqi
 * @date 2023/2/14
 * @description
 */
@Getter
@AllArgsConstructor
public enum MiddleBackDeptAssessRangeEnum implements PullDown {
    RANGE1("[100,null)", "100分（含）以上"),
    RANGE2("[95,100)", "95分（含）-100分"),
    RANGE3("[90,95)", "90分（含）-95分"),
    RANGE4("[85,90)", "85分（含）-90分"),
    RANGE5("[80,85)", "80分（含）-85分"),
    RANGE6("[70,80)", "70分（含）-80分"),
    RANGE7("[60,70)", "60分（含）-70分"),
    RANGE8("[50,60)", "50分（含）-60分"),
    RANGE9("(null,50)", "50分以下");

    private final String formula;
    private final String display;

    @Override
    public String display() {
        return this.display;
    }
}
