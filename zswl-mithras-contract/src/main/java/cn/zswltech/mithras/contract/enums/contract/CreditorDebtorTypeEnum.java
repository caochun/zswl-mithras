package cn.zswltech.mithras.contract.enums.contract;

import cn.zswltech.mithras.foundation.metadata.PullDown;
import lombok.AllArgsConstructor;

/**
 * @author dingqi
 * @date 2022/10/31
 * @description
 */
@AllArgsConstructor
public enum CreditorDebtorTypeEnum implements PullDown {
    CREDITOR("债权人"),
    DEBTOR("债务人");
    ;

    private final String display;

    @Override
    public String display() {
        return display;
    }
}
