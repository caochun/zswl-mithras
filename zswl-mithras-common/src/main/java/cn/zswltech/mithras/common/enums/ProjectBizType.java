package cn.zswltech.mithras.common.enums;

import cn.hutool.core.util.ObjectUtil;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author junke
 */
public enum ProjectBizType {

    ZL("租赁"), BL("保理"), ZZ("转租赁"), ZR("债权转让");

    ProjectBizType(String display) {
        this.display = display;
    }

    public String display;

    private static Map<String, ProjectBizType> map;

    static {
        map = Stream.of(ProjectBizType.values()).collect(Collectors.toMap(ProjectBizType::name, e -> e));
    }

    public static ProjectBizType of(String bizType) {
        if (!map.containsKey(bizType)) {
            return null;
        }
        return map.get(bizType);
    }

    public static String getNameByDisplay(String display) {
        for(ProjectBizType bizType : ProjectBizType.values()) {
            if (ObjectUtil.equals(bizType.display(), display)) {
                return bizType.name();
            }
        }
        return null;
    }

    public String display() {
        return display;
    }

}
