package cn.zswltech.mithras.service.enums.fund;

import cn.zswltech.mithras.service.config.enumscan.IMaterialsTypeConvert;
import cn.zswltech.mithras.common.enums.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 财务资金管理 授信管理
 *
 * @author wangchuanhao
 * @date 2022/8/9 12:48 AM
 */
@AllArgsConstructor
@Getter
public enum FundCreditMaterialsEnum implements PullDown, IMaterialsTypeConvert {

    DEFAULT("资料清单", 1),
    ;

    private final String display;

    /**
     * 排序优先级
     */
    private final int sort;

    private static final Map<String, FundCreditMaterialsEnum> map;

    static {
        map = Stream.of(FundCreditMaterialsEnum.values()).collect(Collectors.toMap(FundCreditMaterialsEnum::name, e -> e));
    }

    public static FundCreditMaterialsEnum getByName(String name) {
        return map.get(name);
    }

    @Override
    public String businessModule() {
        return "FUND_CREDIT";
    }

    @Override
    public String display() {
        return display;
    }
}
