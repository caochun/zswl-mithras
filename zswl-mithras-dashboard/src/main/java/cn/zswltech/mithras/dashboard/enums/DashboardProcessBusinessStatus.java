package cn.zswltech.mithras.dashboard.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Process status codes used by dashboard read models.
 */
@AllArgsConstructor
@Getter
public enum DashboardProcessBusinessStatus {

    RUNNING(1, "审批中"),
    PASS(2, "审批通过"),
    PASS_ALL(6, "一键通过");

    private static final Map<Integer, DashboardProcessBusinessStatus> STATUS_BY_TYPE =
            Stream.of(values()).collect(Collectors.toMap(DashboardProcessBusinessStatus::getType, e -> e));

    private final Integer type;
    private final String display;

    public static DashboardProcessBusinessStatus getByType(Integer type) {
        return STATUS_BY_TYPE.get(type);
    }

    public static String displayOf(Integer type) {
        return Optional.ofNullable(getByType(type))
                .map(DashboardProcessBusinessStatus::getDisplay)
                .orElse("");
    }
}
