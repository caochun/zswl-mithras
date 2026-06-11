package cn.zswltech.mithras.capital.enums.writeoff;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author bigbear
 * @date 2024/9/25 19:26
 * @description
 */
@Getter
@AllArgsConstructor
public enum FinanceFlowTypeEnum {
    PAYMENT("付款"),
    RECEIPT("收款");

    private final String display;
}
