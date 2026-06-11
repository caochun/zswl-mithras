package cn.zswltech.mithras.projectprocess.projlifecycle.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 与模型枚举值一样 cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum
 *
 */
@AllArgsConstructor
@Getter
public enum ProcessEventDescEnum {
    ClientTransferFlow("客户移交创建"),

    ProjEstablishCreateFlow("立项创建审批"),
    ProjEstablishModifyFlow("立项修改审批"),

    ProjReviewCreateFlow("项目评审创建审批"),
    ProjReviewModifyFlow("项目评审修改审批"),
    ProjReviewPricingApprovalFlow("项目评审定价审批"),

    ContractCreateFlow("合同创建审批"),
    ContractModifyFlow("合同变更审批"),
    ContractStartRentFlow("合同起租审批"),
    ContractAddNewReceiptFlow("合同新增借据审批"),
    ContractEarlySettleFlow("合同提前结清审批"),
    ContractNormalSettleFlow("合同正常结清审批"),
    ContractLPRChangeFlow("合同LPR调整审批"),
    ContractExtensionFlow("合同展期审批"),
    ContractEarlyRepayFlow("合同提前还款审批"),
    ContractChangeRepayPlanFlow("合同调整还款计划审批"),

    PaymentCreateFlow("付款申请创建审批"),

    AfterLeaseExtendFlow("租后展期评审"),
    AfterLeaseRepaymentFlow("租后项目调整还款计划审批"),

    RentCollectionExemptionFlow("租金催收减免审批"),
    ;

    private String event;

    private static Map<String, ProcessEventDescEnum> map;

    static {
        map = Stream.of(ProcessEventDescEnum.values()).collect(Collectors.toMap(ProcessEventDescEnum::name, e -> e));
    }

    public static ProcessEventDescEnum getByName(String name) {
        return map.get(name);
    }

}
