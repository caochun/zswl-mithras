package cn.zswltech.mithras.dashboard.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Flow comment types used by dashboard read models.
 */
@AllArgsConstructor
@Getter
public enum DashboardFlowCommentType {

    BH("退回"),
    BHFQR("退回发起人"),
    BHFQR_ZJDW("退回发起人(直接到我)");

    private static final Map<String, DashboardFlowCommentType> TYPE_BY_NAME =
            Stream.of(values()).collect(Collectors.toMap(Enum::name, e -> e));

    private final String message;

    public static DashboardFlowCommentType getByName(String name) {
        return TYPE_BY_NAME.get(name);
    }

    public static String messageOf(String name) {
        return Optional.ofNullable(getByName(name))
                .map(DashboardFlowCommentType::getMessage)
                .orElse("");
    }
}
