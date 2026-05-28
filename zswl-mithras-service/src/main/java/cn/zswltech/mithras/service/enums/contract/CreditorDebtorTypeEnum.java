package cn.zswltech.mithras.service.enums.contract;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
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
