package cn.zswltech.mithras.service.adapter.contract;

import cn.hutool.core.collection.CollUtil;
import cn.zswltech.mithras.contract.core.application.evaluation.ContractEvaluationAgencyLeaseAppraisal;
import cn.zswltech.mithras.contract.core.application.evaluation.ContractEvaluationAgencyLeaseAppraisalPort;
import cn.zswltech.mithras.leaseholdproperty.application.LeaseAppraisalService;
import cn.zswltech.mithras.leaseholdproperty.infrastructure.persistence.mapper.TycAppraisalCompanyBaseInfoMapper;
import cn.zswltech.mithras.leaseholdproperty.infrastructure.persistence.mapper.model.LeaseItemAppraisalRelation;
import cn.zswltech.mithras.leaseholdproperty.infrastructure.persistence.mapper.model.TycAppraisalCompanyBaseInfo;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ContractEvaluationAgencyLeaseAppraisalPortAdapter implements ContractEvaluationAgencyLeaseAppraisalPort {

    @Resource
    private LeaseAppraisalService leaseAppraisalService;
    @Resource
    private TycAppraisalCompanyBaseInfoMapper companyBaseInfoMapper;

    @Override
    public List<ContractEvaluationAgencyLeaseAppraisal> queryLatestListByContractId(Long contractId) {
        List<LeaseItemAppraisalRelation> relationList = leaseAppraisalService.queryLastestListByContractId(contractId);
        if (CollUtil.isEmpty(relationList)) {
            return Collections.emptyList();
        }
        return relationList.stream().map(item -> {
            ContractEvaluationAgencyLeaseAppraisal appraisal = new ContractEvaluationAgencyLeaseAppraisal();
            appraisal.setLeaseItemId(item.getLeaseItemId());
            appraisal.setCompanyId(item.getCompanyId());
            appraisal.setPurpose(item.getPurpose());
            appraisal.setSelectType(item.getSelectType());
            return appraisal;
        }).collect(Collectors.toList());
    }

    @Override
    public Map<Long, String> companyIdNameMap(List<Long> companyIds) {
        if (CollUtil.isEmpty(companyIds)) {
            return Collections.emptyMap();
        }
        List<TycAppraisalCompanyBaseInfo> companyList = companyBaseInfoMapper.selectBatchIds(companyIds);
        if (CollUtil.isEmpty(companyList)) {
            return Collections.emptyMap();
        }
        return companyList.stream().collect(Collectors.toMap(
                TycAppraisalCompanyBaseInfo::getId,
                TycAppraisalCompanyBaseInfo::getCompanyName,
                (left, right) -> left
        ));
    }
}
