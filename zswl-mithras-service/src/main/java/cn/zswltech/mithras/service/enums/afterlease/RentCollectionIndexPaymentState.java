package cn.zswltech.mithras.service.enums.afterlease;

import cn.zswltech.mithras.common.enums.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 租金催收首页付款申请状态
 *
 * @author wangchuanhao
 * @date 2022/11/17 4:06 PM
 */
@AllArgsConstructor
@Getter
public enum RentCollectionIndexPaymentState implements PullDown {

    TAKE_EFFECT("生效"),
    START_RENT("起租"),
    SETTLE("结清"),
    OVERDUE("逾期"),
    ;

    private String display;

    private static Map<String, RentCollectionIndexPaymentState> map;

    static {
        map = Stream.of(RentCollectionIndexPaymentState.values()).collect(Collectors.toMap(RentCollectionIndexPaymentState::name, e -> e));
    }

    @Override
    public String display() {
        return display;
    }

    public static RentCollectionIndexPaymentState of(String name) {
        return map.get(name);
    }
}
