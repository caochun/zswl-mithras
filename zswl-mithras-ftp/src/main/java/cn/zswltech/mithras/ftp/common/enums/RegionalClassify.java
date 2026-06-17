package cn.zswltech.mithras.ftp.common.enums;

import cn.zswltech.mithras.foundation.metadata.PullDown;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum RegionalClassify implements PullDown {
    ZHEJIANG("浙江地区"),
    ENCOURAGE("鼓励支持类地区"),
    OTHER("其他地区");

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

    public static RegionalClassify getProjRegionalClassify(String proj) {
        if (ZHEJIANG.name().equals(proj)) {
            return ZHEJIANG;
        }
        if ("ENCOURAGE_SUPPORT".equals(proj)) {
            return ENCOURAGE;
        }
        return OTHER;
    }

    public static RegionalClassify getProjRegionDivision(String regionDivision) {
        if (Objects.isNull(regionDivision)) {
            return null;
        }
        switch (regionDivision) {
            case "ZJ_AREA":
            case "PUBLIC_ZJ_AREA":
                return ZHEJIANG;
            case "ONE_AREA":
            case "PUBLIC_ENCOURAGE":
                return ENCOURAGE;
            default:
                return OTHER;
        }
    }
}
