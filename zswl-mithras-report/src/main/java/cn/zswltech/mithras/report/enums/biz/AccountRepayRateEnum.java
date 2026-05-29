package cn.zswltech.mithras.report.enums.biz;

import cn.zswltech.mithras.common.enums.PullDown;
import cn.zswltech.mithras.service.config.enumscan.PullDownExt;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 账户表还款频率
 *
 * @author wangchuanhao
 * @date 2022/10/10 10:30 AM
 */
@AllArgsConstructor
@Getter
@PullDownExt("crAccountRepayRate")
public enum AccountRepayRateEnum implements PullDown {

    MONTH("10","月"),
    DOUBLE_MONTH("21", "双月"),
    QUARTER("22", "季"),
    HALF_YEAR("23", "半年"),
    YEAR("24", "年"),
    LRREGULAR("40", "还款间隔不固定"),
    NON_STAGES("99", "其他"),

    ;

    private String value;
    private String display;

    private static Map<String, AccountRepayRateEnum> nameMap;

    static {
        nameMap = Stream.of(AccountRepayRateEnum.values()).collect(Collectors.toMap(AccountRepayRateEnum::name, Function.identity(), (k1, k2)->k1));
    }

    public static String convert(String originCode) {
        return Optional.ofNullable(nameMap.get(originCode)).map(AccountRepayRateEnum::getValue).orElse(null);
    }

    public static AccountRepayRateEnum findByValue(String value) {
        for (AccountRepayRateEnum accountRepayRateEnum : AccountRepayRateEnum.values()) {
            if (accountRepayRateEnum.getValue().equals(value)) {
                return accountRepayRateEnum;
            }
        }
        return null;
    }

    @Override
    public String display() {
        return display;
    }

    @Override
    public String valueKey() {
        return value;
    }
}
