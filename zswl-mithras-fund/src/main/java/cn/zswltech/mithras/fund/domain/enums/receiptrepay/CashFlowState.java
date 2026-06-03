package cn.zswltech.mithras.fund.domain.enums.receiptrepay;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import cn.zswltech.mithras.service.config.enumscan.PullDownExt;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 还款状态枚举
 *
 * @author wangchuanhao
 * @date 2023/2/20 11:40 AM
 */
@AllArgsConstructor
@Getter
@PullDownExt("fundReceiptRepayCashFlowState")
public enum CashFlowState implements PullDown {

    NO_WRITE_OFF("未核销"),
    WRITE_OFF_ING("核销中"),
    PART_WRITE_OFF("部分核销"),
    WRITTEN_OFF("核销完毕"),
    BEYOND_WRITTEN_OFF("超额核销")

    ;

    private String display;

    private static Map<String, CashFlowState> map;
    static {
        map = Stream.of(CashFlowState.values()).collect(Collectors.toMap(CashFlowState::name, e -> e));
    }

    public static CashFlowState of(String name) {
        return map.get(name);
    }

    @Override
    public String display() {
        return display;
    }
}
