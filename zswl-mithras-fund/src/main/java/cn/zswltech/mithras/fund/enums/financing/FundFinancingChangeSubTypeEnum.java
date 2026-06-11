package cn.zswltech.mithras.fund.enums.financing;

import cn.zswltech.mithras.foundation.metadata.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author dingqi
 * @date 2023/2/20
 * @description
 */
@AllArgsConstructor
@Getter
public enum FundFinancingChangeSubTypeEnum implements PullDown {
    CHANGE_LPR("LPR调整"),
    CHANGE_EARLY_SETTLE("提前还款"),
    CHANGE_OTHER("其他");

    private final String display;

    @Override
    public String display() {
        return this.display;
    }
}
