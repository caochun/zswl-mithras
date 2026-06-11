package cn.zswltech.mithras.projectprocess.enums.projestablish;

import cn.zswltech.mithras.foundation.metadata.PullDown;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author zhaozhengkang
 * @description 支付方式枚举
 * @since
 */
public enum PayType implements PullDown {

    /**
     * 先付
     */
    ADVANCED("先付（非贴现）"),
    /**
     * 后付
     */
    AFTERWARD("后付");

    public String display;

    PayType(String display) {
        this.display = display;
    }

    private static final Map<String, PayType> map;

    static {
        map = Stream.of(PayType.values()).collect(Collectors.toMap(PayType::name, e -> e));
    }

    public static PayType of(String payType) {
        return map.get(payType);
    }

    @Override
    public String display() {
        return display;
    }
}
