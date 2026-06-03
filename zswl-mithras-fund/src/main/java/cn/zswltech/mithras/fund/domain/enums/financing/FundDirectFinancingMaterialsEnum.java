package cn.zswltech.mithras.fund.domain.enums.financing;

import cn.zswltech.mithras.service.config.enumscan.IMaterialsTypeConvert;
import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AllArgsConstructor
@Getter
public enum FundDirectFinancingMaterialsEnum implements PullDown, IMaterialsTypeConvert {

    //PROSPECTUS("募集说明书", 1),
    RECORD("备案材料", 1),
    AGENCY("中介机构材料", 2),
    OTHER("其他", 3),

    ;

    private final String display;

    /**
     * 排序优先级
     */
    private Integer sort;

    private static final Map<String, FundDirectFinancingMaterialsEnum> map;

    static {
        map = Stream.of(FundDirectFinancingMaterialsEnum.values()).collect(Collectors.toMap(FundDirectFinancingMaterialsEnum::name, e -> e));
    }

    public static FundDirectFinancingMaterialsEnum getByName(String name) {
        return map.get(name);
    }

    public static List<String> listAll() {
        return new ArrayList<>(map.keySet());
    }

    @Override
    public String businessModule() {
        return "FUND_FINANCING";
    }

    @Override
    public String display() {
        return display;
    }
}
