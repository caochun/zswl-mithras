package cn.zswltech.mithras.service.enums.fund.receiptrepay;

import cn.zswltech.mithras.common.enums.PullDown;
import cn.zswltech.mithras.service.config.enumscan.PullDownExt;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 付款状态枚举
 * 产品要把本月的还款的状态放到付款状态里处理
 * 理论上就=本期的还款状态，但有可能还款同时存在两种状态
 * 未核销 + 核销中 + 核销完毕 = 未核销
 * 未核销 + 核销中 = 核销中
 * 核销中 + 核销完毕 = 核销中
 * 未核销 + 核销完毕 = 未核销
 * @author wangchuanhao
 * @date 2023/2/20 11:40 AM
 */
@AllArgsConstructor
@Getter
@PullDownExt("fundReceiptRepayState")
public enum ReceiptRepayState implements PullDown {

//    CLOSED("已关闭"),
    NO_WRITE_OFF("未核销"),
    WRITE_OFF_ING("核销中"),
    WRITTEN_OFF("核销完毕"),
    SETTLE("结清"),

    ;

    private String display;

    private static Map<String, ReceiptRepayState> map;
    static {
        map = Stream.of(ReceiptRepayState.values()).collect(Collectors.toMap(ReceiptRepayState::name, e -> e));
    }

    public static ReceiptRepayState of(String name) {
        return map.get(name);
    }

    @Override
    public String display() {
        return display;
    }
}
