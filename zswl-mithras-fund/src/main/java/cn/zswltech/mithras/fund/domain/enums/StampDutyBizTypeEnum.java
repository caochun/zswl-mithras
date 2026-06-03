package cn.zswltech.mithras.fund.domain.enums;

import cn.zswltech.mithras.service.config.enumscan.IMaterialsTypeConvert;
import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author luyujie
 * @date 2026/1/15
 * @description
 */
@AllArgsConstructor
@Getter
public enum StampDutyBizTypeEnum implements PullDown, IMaterialsTypeConvert {
    RZZLHT("融资租赁合同", 10),
    MMHT("买卖合同", 20),
    ZLHT("租赁合同", 30),
    JKHT("借款合同", 40),
    JSHT("技术合同", 50),
    CCBXHT("财产保险合同", 60),
    JSGCHT("建设工程合同", 70),
    CLHT("承揽合同", 80),
    YSHT("运输合同", 90),
    BGHT("保管合同", 100),
    CCHT("仓储合同", 110),
    YYZB("营业账簿", 120),
    CQZYSJ("产权转移书据", 130),
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
        return "STAMP_DUTY";
    }

    @Override
    public String display() {
        return display;
    }
}
