package cn.zswltech.mithras.dashboard.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Dashboard read-side process model keys.
 *
 * <p>These constants mirror stable workflow model keys used as dashboard query
 * dimensions. Dashboard should not depend on workflow's internal registration
 * enum only to render labels or build read-model filters.
 */
@AllArgsConstructor
@Getter
public enum DashboardProcessModel {

    ClientModifyFlow("客户变更", "CLIENT"),
    ClientTransferFlow("客户移交", "CLIENT_TRANSFER"),
    ProjEstablishCreateFlow("项目立项创建", "PROJ_ESTABLISH"),
    ProjReviewCreateFlow("项目评审创建", "PROJ_REVIEW"),
    ContractCreateFlow("合同创建", "CONTRACT"),
    ContractModifyFlow("合同其他变更", "CONTRACT"),
    ContractEarlySettleFlow("合同提前结清", "CONTRACT"),
    ContractNormalSettleFlow("合同正常结清", "CONTRACT"),
    ContractEarlyRepayFlow("合同提前还款", "CONTRACT"),
    PaymentCreateFlow("付款申请", "PAYMENT"),
    PaymentActualDetailFlow("付款实际核销确认", "PAYMENT"),
    LeaseCreateFlow("租赁物创建", "LEASE"),
    LeaseModifyFlow("租赁物变更", "LEASE"),
    NewAfterLeaseCheckReportCommonlyFlow("租后检查报告(一般检查)", "NEW_AFTER_LEASE_CHECK_REPORT");

    private static final Map<String, DashboardProcessModel> MODEL_BY_KEY =
            Stream.of(values()).collect(Collectors.toMap(Enum::name, e -> e));

    private final String display;
    private final String businessModuleName;

    public static DashboardProcessModel getByName(String name) {
        return MODEL_BY_KEY.get(name);
    }

    public static String displayOf(String modelKey) {
        return Optional.ofNullable(getByName(modelKey))
                .map(DashboardProcessModel::getDisplay)
                .orElse("");
    }
}
