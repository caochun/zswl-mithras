package cn.zswltech.mithras.contract.core.evaluation;

import lombok.Data;

/**
 * 合同评估机构草稿需要的租赁物评估关系快照。
 */
@Data
public class ContractEvaluationAgencyLeaseAppraisal {

    private Long leaseItemId;

    private Long companyId;

    private String purpose;

    private String selectType;
}
