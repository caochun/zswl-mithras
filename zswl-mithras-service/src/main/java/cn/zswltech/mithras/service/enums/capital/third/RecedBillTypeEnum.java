package cn.zswltech.mithras.service.enums.capital.third;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author yangxiong
 * @date 2024/5/23/17:31
 * @description
 */
@Getter
@AllArgsConstructor
public enum RecedBillTypeEnum {
    SETTLEMENT_BILL("6E41E17C", "结算单"),
    PAYMENT_BILL("cas_paybill", "付款单"),
    RECEIPT_BILL("recbill", "收款单"),
    DOWN_PAYMENT("5E920865", "下拨单"),
    TRANSFER_UP("D125C4DE", "上划单"),
    AGENT_PAYMENT_BILL("cas_agentpaybill", "代发单"),
    OTHER("other", "其它单据"),
    ;

    private final String code;
    private final String display;
}
