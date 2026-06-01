package cn.zswltech.mithras.service.enums.projestablish;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author junke
 */
@Getter
public enum LeaseType implements PullDown {
    hui_zu("回租"), zhi_zu("直租"), jyx_zu("经营性租赁");

    LeaseType(String display) {
        this.display = display;
    }

    public final String display;

    private static Map<String, LeaseType> map;

    static {
        map = Stream.of(LeaseType.values()).collect(Collectors.toMap(LeaseType::name, e -> e));
    }

    public static String getNameByDisplay(String display) {
        for (LeaseType leaseEnum : LeaseType.values()) {
            if (leaseEnum.getDisplay().equals(display)) {
                return leaseEnum.name();
            }
        }
        return null;
    }

    public static LeaseType of(String bizType) {
        return map.get(bizType);
    }

    @Override
    public String display() {
        return display;
    }
}
