package cn.zswltech.mithras.contract.enums.contract;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author dingqi
 * @date 2022/8/24
 * @description
 */
@Getter
@AllArgsConstructor
public enum ContractSettlePlanTypeEnum implements PullDown {
    SETTLE_NORMAL("正常结清"),
    SETTLE_IN_ADVANCE("提前结清");

    public final String display;

    @Override
    public String display() {
        return display;
    }
}
