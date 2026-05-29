package cn.zswltech.mithras.service.enums.afterlease;

import cn.zswltech.mithras.common.enums.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 租金催收首页合同状态
 *
 * @author wangchuanhao
 * @date 2022/11/17 4:06 PM
 */
@AllArgsConstructor
@Getter
public enum RentCollectionIndexContractStatus implements PullDown {

    TAKE_EFFECT("生效"),
    START_RENT("起租"),
    SETTLE("结清"),
    ALL("全部"),
    ;

    private String display;

    private static Map<String, RentCollectionIndexContractStatus> map;

    static {
        map = Stream.of(RentCollectionIndexContractStatus.values()).collect(Collectors.toMap(RentCollectionIndexContractStatus::name, e -> e));
    }

    @Override
    public String display() {
        return display;
    }

    public static RentCollectionIndexContractStatus of(String name) {
        return map.get(name);
    }

    /**
     * 转换为合同状态
     * @return
     */
    public static String convert(String originCode) {
        RentCollectionIndexContractStatus r = map.getOrDefault(originCode, ALL);
        return ALL.equals(r) ? null : r.name();
    }

}
