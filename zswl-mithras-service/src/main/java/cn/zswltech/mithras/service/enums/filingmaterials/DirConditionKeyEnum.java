package cn.zswltech.mithras.service.enums.filingmaterials;

import lombok.AllArgsConstructor;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;


@AllArgsConstructor
public enum DirConditionKeyEnum {
    APPROVAL_CREDIT("APPROVAL_CREDIT", "立项审批流程", "GroupCreditEstablishCreateFlow",1),
    APPROVAL_CHANGE_CREDIT("APPROVAL_CHANGE_CREDIT", "立项变更审批流程", "GroupCreditEstablishModifyFlow",2),

    APPROVAL_CREDIT_REVIEW("APPROVAL_CREDIT_REVIEW", "授信评审审批流程", "GroupCreditReviewCreateFlow",3),
    APPROVAL_CHANGE_CREDIT_REVIEW("APPROVAL_CHANGE_CREDIT_REVIEW", "授信评审变更审批流程", "GroupCreditReviewModifyFlow",4),

    APPROVAL_PROJECT("APPROVAL_PROJECT", "立项审批流程", "ProjEstablishCreateFlow",5),
    APPROVAL_CHANGE_PROJECT("APPROVAL_CHANGE_PROJECT", "立项变更审批流程", "ProjEstablishModifyFlow",6),

    APPROVAL_PROJECT_REVIEW("APPROVAL_PROJECT_REVIEW", "项目评审流程", "ProjReviewCreateFlow",7),
    APPROVAL_CHANGE_PROJECT_REVIEW("APPROVAL_CHANGE_PROJECT_REVIEW", "项目评审变更流程", "ProjReviewModifyFlow",8),

    APPROVAL_LEASEHOLD("APPROVAL_LEASEHOLD", "租赁物审批流程", "LeaseCreateFlow",9),
    APPROVAL_LEASEHOLD_REVIEW("APPROVAL_LEASEHOLD_REVIEW", "租赁物变更审批流程", "LeaseModifyFlow",10),

    APPROVAL_PROJECT_PRICING("APPROVAL_PROJECT_PRICING", "项目定价审批流程", "ProjReviewPricingApprovalFlow",11),
    APPROVAL_CHANGE_PROJECT_PRICING("APPROVAL_CHANGE_PROJECT_PRICING", "项目定价变更审批流程", "ProjReviewPricingModifyApprovalFlow",12),

    // 扩展条件（无需新增类，仅加枚举项）
    APPROVAL_CONTRACT("APPROVAL_CONTRACT", "合同签署审批流程", "ContractCreateFlow",13),
    APPROVAL_CHANGE_CONTRACT("APPROVAL_CHANGE_CONTRACT", "合同其他变更审批流程", "ContractModifyFlow",14),

    APPROVAL_PAYMENT("APPROVAL_PAYMENT", "付款审批流程", "PaymentCreateFlow",15),

    FILE_CHECK("FILE_CHECK", "校验目录下文件是否存在", "",16);


    private final String code;
    private final String name;
    private final String queryType;
    private final int sort;


    public static DirConditionKeyEnum getDirConditionKeyByConditionKey(String conditionKey) {
        for (DirConditionKeyEnum value : DirConditionKeyEnum.values()) {
            if (value.getCode().equals(conditionKey)) {
                return value;
            }
        }
        return null;
    }
    public static Map<String, Integer> getSortMap(){
        return Arrays.stream(DirConditionKeyEnum.values()).collect(Collectors.toMap(DirConditionKeyEnum::getQueryType, DirConditionKeyEnum::getSort));
    }
    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getQueryType() {
        return queryType;
    }

    public int getSort() {
        return sort;
    }
}
