package cn.zswltech.mithras.service.enums.fund.receiptrepay;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 状态 数据类型
 *
 * @author wangchuanhao
 * @date 2023/2/20 11:40 AM
 */
@AllArgsConstructor
@Getter
public enum StateDataType {

    /**
     * 付款
     */
    RECEIPT_REPAY,

    /**
     * 还款
     */
    CASH_FLOW,

    ;

    private static Map<String, StateDataType> map;
    static {
        map = Stream.of(StateDataType.values()).collect(Collectors.toMap(StateDataType::name, e -> e));
    }

    public static StateDataType of(String name) {
        return map.get(name);
    }

}
