package cn.zswltech.mithras.service.enums.riskcontrol;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.service.config.enumscan.PullDown;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @description: 风控行业分类
 * @author: zhaozhengkang
 * @date: 2023/2/9 14:33
 */
public enum RiskControlIndustryClassify implements PullDown {
    /**
     * 公用事业类
     */
    PUBLIC_UTILITIES("公用事业类"),
    /**
     * 民生消费类（含供水供热供电供气、污水处理等）
     */
    CIVIL_CONSUMPTION("民生消费类（含供水供热供电供气、污水处理等）"),
    /**
     * 旅游行业
     */
    TRAVEL("旅游行业"),
    /**
     * 钢铁、不锈钢及有色金属冶炼行业
     */
    STEEL("钢铁、不锈钢及有色金属冶炼行业"),
    /**
     * 交通运输物流行业（含冷链仓储物流、汽车经销商、普通物流、公共交通等）
     */
    TRANSPORTATION_LOGISTICS("交通运输物流行业（含冷链仓储物流、汽车经销商、普通物流、公共交通等）"),
    /**
     * 水上运输业，即航运行业
     */
    WATER_TRANSPORTATION("水上运输业"),
    /**
     * 造纸、精细化工、汽车零部件等传统制造行业
     */
    PAPER_MAKING("造纸、精细化工、汽车零部件等传统制造行业"),
//    /**
//     * 精细化工行业
//     * 合并入造纸、精细化工、汽车零部件等传统制造行业
//     */
//    FINE_CHEMICAL_COMMON("精细化工行业（非长三角地区）"),
//    /**
//     * 精细化工行业（仅长三角地区）
//     * 合并入造纸、精细化工、汽车零部件等传统制造行业
//     */
//    FINE_CHEMICAL("精细化工行业（仅长三角地区）"),
    /**
     * 建筑工程行业
     */
    CONSTRUCTION("建筑工程行业（含建筑材料）"),
    /**
     * 信息产业（5G、IDC、通信服务等）
     */
    INFORMATION_INDUSTRY("信息产业（5G、IDC、通信服务等新基建行业）"),
    /**
     * 新能源、新材料、新科技等智能制造、先进装备制造行业
     */
    NEW_MATERIALS("新能源、新材料、新科技等智能制造、先进装备制造行业"),
//    /**
//     * 新材料、新科技、智能制造等高端装备制造行业（仅长三角地区）
//     * 合并入新能源、新材料、新科技等智能制造、先进装备制造行业
//     */
//    NEW_MATERIALS_CHANGJIANG_DELTA("新材料、新科技、智能制造等高端装备制造行业（仅长三角地区）"),
//    /**
//     * 其他（取国标行业分类第二级）
//     */
//    OTHER("其他（取国标行业分类第二级）"),
//    /**
//     * 工程机械类（厂家担保模式）
//     */
//    ENGINEERING_MACHINERY("工程机械类（厂家担保模式）"),
//    /**
//     * 工程机械类（非厂家担保模式）
//     */
//    ENGINEERING_MACHINERY_NON("工程机械类（非厂家担保模式）"),
//    /**
//     * 新能源 -- 风控调整
//     * 合并入新能源、新材料、新科技等智能制造、先进装备制造行业
//     */
//    SHIP_SOLAR("新能源"),
    /**
     * 创新业务
     */
    INNOVATION_BUSINESS("创新业务（取国标行业分类第二级）"),
    /**
     * 集团内协同业务
     */
    INTRA_GROUP_COLLABORATION("集团内协同业务"),
    /**
     * 民办教育行业
     */
    NON_GOVERNMENT_FUNDED_EDUCATION("民办教育行业"),

    /**
     * 其他行业
     */
    OTHER("其他行业"),

    /**
     * 国有控股产业
     */
    PUBLIC_HOLDING_COMPANY_INDUSTRY("国有控股产业"),
    ;
    private static HashMap<String, RiskControlIndustryClassify> map = new HashMap<>();

    static {
        for (RiskControlIndustryClassify value : RiskControlIndustryClassify.values()) {
            map.put(value.display, value);
        }
    }

    private final String display;

    RiskControlIndustryClassify(String display) {
        this.display = display;
    }

    public static RiskControlIndustryClassify findByName(String name) {
        for (RiskControlIndustryClassify item : values()) {
            if (item.name().equals(name)) {
                return item;
            }
        }
        return null;
    }

    public static RiskControlIndustryClassify findByDisplay(String display) {
        for (RiskControlIndustryClassify item : values()) {
            if (item.display.equals(display)) {
                return item;
            }
        }
        return null;
    }

    public static String display2Name(String display) {
        if (ObjectUtil.isEmpty(display)) {
            return INNOVATION_BUSINESS.name();
        }
        return map.getOrDefault(display, INNOVATION_BUSINESS).name();
    }

    @Override
    public String display() {
        return display;
    }

    private static Map<String, RiskControlIndustryClassify> ofmap;

    static {
        ofmap = Stream.of(RiskControlIndustryClassify.values())
                .collect(Collectors.toMap(RiskControlIndustryClassify::name, e -> e));
    }

    public static RiskControlIndustryClassify of(String name) {
        return ofmap.get(name);
    }

    public static List<String> listPublic() {
        return ListUtil.of(
                PUBLIC_UTILITIES.name(),
                CIVIL_CONSUMPTION.name(),
                TRAVEL.name()
        );
    }

    public static List<String> listIndustry() {
        List<String> publicList = listPublic();
        return map.values().stream().map(Enum::name).filter(e -> !publicList.contains(e)).collect(Collectors.toList());
    }
}
