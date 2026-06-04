package cn.zswltech.mithras.projectprocess.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author dingqi
 * @date 2025/7/18
 * @description
 */
@Getter
@AllArgsConstructor
public enum TradeStructureRoleEnum {
    LESSEE("承租人"),
    GUARANTOR("担保人"),
    MORTGAGE("抵押人"),
    PLEDGE("质押人"),
    CREDITOR("债权人"),
    DEBTOR("债务人");

    private final String display;
}
