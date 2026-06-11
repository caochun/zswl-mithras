package cn.zswltech.mithras.capital.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author yangxiong
 * @date 2024/5/29/12:52
 * @description
 */
@Getter
@AllArgsConstructor
public enum FinanceFlowDetailTableEnum {
    PAYMENT_ACTUAL_DETAIL("付款核销详情表"),
    COLLECTION_RECORD_INFO("收款记录详情表"),
    NETTING_REFUND("轧差退款表"),
    MARGIN_RECORD_INFO("保证金记录详情表"),
    WARRANTY_RECORD_INFO("质保金记录详情表"),
    FUND_RECEIPT_FLOW_DETAIL("资金端核销详情表")
    ;

    private final String display;

    private static Map<String, FinanceFlowDetailTableEnum> map;

    static {
        map = Stream.of(FinanceFlowDetailTableEnum.values()).collect(Collectors.toMap(FinanceFlowDetailTableEnum::name, e -> e, (a, b) -> a));
    }

    public static FinanceFlowDetailTableEnum of(String name) {
        return map.get(name);
    }


}
