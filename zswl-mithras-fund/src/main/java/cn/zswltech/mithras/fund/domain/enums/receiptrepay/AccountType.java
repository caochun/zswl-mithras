package cn.zswltech.mithras.fund.domain.enums.receiptrepay;

import cn.zswltech.mithras.service.config.enumscan.PullDown;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/2/20 17:14
 */
public enum AccountType implements PullDown {

    /**
     * 收款账户
     */
    RECEIPT("收款账户"),

    /**
     * 还款账户
     */
    REPAY("还款账户"),

    ;
    private final String display;
    private static Map<String, AccountType> map;
    static {
        map = Stream.of(AccountType.values()).collect(Collectors.toMap(AccountType::name, e -> e));
    }

    AccountType(String display) {
        this.display = display;
    }

    public static AccountType of(String name) {
        return map.get(name);
    }

    @Override
    public String display() {
        return this.display;
    }
}
