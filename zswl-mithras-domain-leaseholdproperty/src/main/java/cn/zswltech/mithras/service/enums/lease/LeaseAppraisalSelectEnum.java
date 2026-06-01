package cn.zswltech.mithras.service.enums.lease;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;


@AllArgsConstructor
@Getter
public enum LeaseAppraisalSelectEnum implements PullDown {
    SELECTED("选定"),
    NOT_SELECTED("未选定"),
    ;
    private final String display;

    public static LeaseAppraisalSelectEnum of(String name) {
        for (LeaseAppraisalSelectEnum item : values()) {
            if (Objects.equals(item.name(), name)) {
                return item;
            }
        }
        return null;
    }

    @Override
    public String display() {
        return display;
    }
}
