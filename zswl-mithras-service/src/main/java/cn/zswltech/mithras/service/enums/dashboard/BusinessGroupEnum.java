package cn.zswltech.mithras.service.enums.dashboard;

import cn.hutool.core.collection.ListUtil;
import cn.zswltech.mithras.service.config.enumscan.PullDown;
import cn.zswltech.mithras.service.enums.riskcontrol.RiskControlIndustryClassify;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//公用类（民生消费+旅游+公用事业）、产业类
@AllArgsConstructor
@Getter
public enum BusinessGroupEnum implements PullDown {
    PUBLIC_CATEGORY("公用类", ListUtil.toList(RiskControlIndustryClassify.PUBLIC_UTILITIES.name(), RiskControlIndustryClassify.CIVIL_CONSUMPTION.name(), RiskControlIndustryClassify.TRAVEL.name())),
    INDUSTRY_CATEGORY("产业类", null),
    ;

    private final String display;

    private final List<String> riskControlIndustryClassifys;
    private static Map<String, BusinessGroupEnum> map;

    static {
        map = Stream.of(BusinessGroupEnum.values()).collect(Collectors.toMap(BusinessGroupEnum::name, e -> e, (k1, k2)-> k1));
    }

    public static BusinessGroupEnum getBusinessByRiskControlIndustryClassify(String riskControlIndustryClassify){
        if(PUBLIC_CATEGORY.riskControlIndustryClassifys.contains(riskControlIndustryClassify)){
            return PUBLIC_CATEGORY;
        }else {
            return INDUSTRY_CATEGORY;
        }
    }

    public static BusinessGroupEnum of(String name) {
        return map.get(name);
    }

    public static String getNameByDisplay(String display) {
        for (BusinessGroupEnum groupEnum : BusinessGroupEnum.values()) {
            if (groupEnum.getDisplay().equals(display)) {
                return groupEnum.name();
            }
        }
        return null;
    }

    @Override
    public String display() {
        return display;
    }

}
