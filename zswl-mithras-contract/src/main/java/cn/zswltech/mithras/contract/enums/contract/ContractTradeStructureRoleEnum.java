package cn.zswltech.mithras.contract.enums.contract;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 合同交易结构角色。
 */
@Getter
@AllArgsConstructor
public enum ContractTradeStructureRoleEnum {
    LESSEE("承租人"),
    GUARANTOR("担保人"),
    MORTGAGE("抵押人"),
    PLEDGE("质押人"),
    CREDITOR("债权人"),
    DEBTOR("债务人");

    private final String display;
}
