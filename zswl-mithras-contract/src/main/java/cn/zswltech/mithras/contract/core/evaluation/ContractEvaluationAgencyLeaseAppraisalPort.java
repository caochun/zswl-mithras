package cn.zswltech.mithras.contract.core.evaluation;

import java.util.List;
import java.util.Map;

public interface ContractEvaluationAgencyLeaseAppraisalPort {

    List<ContractEvaluationAgencyLeaseAppraisal> queryLatestListByContractId(Long contractId);

    Map<Long, String> companyIdNameMap(List<Long> companyIds);
}
