package cn.zswltech.mithras.projectprocess.enums.newftp;

import cn.zswltech.mithras.foundation.metadata.PullDown;
import cn.zswltech.mithras.projectprocess.enums.projreview.ProjRegionalClassify;
import cn.zswltech.mithras.projectprocess.enums.projreview.ProjRegionalDivisionEnum;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @description: 地区分类
 * @author: zhaozhengkang
 * @date: 2023/5/19 11:26
 * ftp使用
 */
public enum RegionalClassify implements PullDown {
    /**
     * 浙江地区
     */
    ZHEJIANG("浙江地区"),

    /**
     * 鼓励支持类地区（除浙江）
     */
    ENCOURAGE("鼓励支持类地区"),

    /**
     * 其他地区
     */
    OTHER("其他地区"),

    //ENCOURAGE("鼓励支持类地区（除浙江）"),
    ;

    private final String display;

    RegionalClassify(String display) {
        this.display = display;
    }

    @Override
    public String display() {
        return display;
    }

    private static Map<String, RegionalClassify> map;

    static {
        map = Stream.of(RegionalClassify.values())
                .collect(Collectors.toMap(RegionalClassify::name, e -> e));
    }

    public static RegionalClassify of(String name) {
        return map.get(name);
    }

    public static RegionalClassify getProjRegionalClassify(String proj){
        if(ZHEJIANG.name().equals(proj)){
            return ZHEJIANG;
        } else if(ProjRegionalClassify.ENCOURAGE_SUPPORT.name().equals(proj)){
            return ENCOURAGE;
        } else {
            return OTHER;
        }
    }

    public static RegionalClassify getProjRegionDivision(String regionDivision) {
        ProjRegionalDivisionEnum projRegionalDivisionEnum = ProjRegionalDivisionEnum.of(regionDivision);
        if (Objects.isNull(projRegionalDivisionEnum)) {
            return null;
        }
        switch (projRegionalDivisionEnum) {
            case ZJ_AREA:
            case PUBLIC_ZJ_AREA: {
                return RegionalClassify.ZHEJIANG;
            }
            case ONE_AREA:
            case PUBLIC_ENCOURAGE: {
                return RegionalClassify.ENCOURAGE;
            }
            default: {
                return RegionalClassify.OTHER;
            }
        }
    }
}
