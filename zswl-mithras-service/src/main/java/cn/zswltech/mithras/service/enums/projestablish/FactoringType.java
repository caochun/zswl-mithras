package cn.zswltech.mithras.service.enums.projestablish;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import cn.zswltech.mithras.service.enums.common.ProjectBizType;
import cn.zswltech.mithras.service.enums.dashboard.BusinessGroupEnum;
import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author junke
 */
@Getter
public enum FactoringType implements PullDown {

    yzmbl("有追明保理"), wzmbl("无追明保理"), yzabl("有追暗保理");

    FactoringType(String display) {
        this.display = display;
    }

    public final String display;

    @Override
    public String display() {
        return display;
    }

    private static final Map<String, FactoringType> map;

    static {
        map = Stream.of(FactoringType.values()).collect(Collectors.toMap(FactoringType::name, e -> e));
    }

    public static String getNameByDisplay(String display) {
        for (FactoringType factoringEnum : FactoringType.values()) {
            if (factoringEnum.getDisplay().equals(display)) {
                return factoringEnum.name();
            }
        }
        return null;
    }

    public static FactoringType of(String name) {
        return map.get(name);
    }
}
