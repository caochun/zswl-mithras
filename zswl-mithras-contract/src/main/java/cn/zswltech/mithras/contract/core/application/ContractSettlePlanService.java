package cn.zswltech.mithras.contract.core.application;

import cn.zswltech.mithras.contract.mapper.model.contract.ContractSettlePlan;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author dingqi
 * @date 2022/8/24
 * @description
 */
public interface ContractSettlePlanService extends IService<ContractSettlePlan> {
    void removeByContractId(Long contractId);

    ContractSettlePlan getLatestContractSettlePlan(Long contractId);

    Long saveOrUpdatePlan(ContractSettlePlan contractSettlePlan);
}
