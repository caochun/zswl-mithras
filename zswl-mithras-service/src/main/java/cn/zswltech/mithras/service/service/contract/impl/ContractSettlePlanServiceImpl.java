package cn.zswltech.mithras.service.service.contract.impl;

import cn.zswltech.mithras.service.enums.contract.ContractProcessStatusEnum;
import cn.zswltech.mithras.service.mapper.contract.ContractSettlePlanMapper;
import cn.zswltech.mithras.service.mapper.model.contract.ContractSettlePlan;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.contract.ContractService;
import cn.zswltech.mithras.service.service.contract.ContractSettlePlanService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author dingqi
 * @date 2022/8/24
 * @description
 */
@Service
public class ContractSettlePlanServiceImpl extends ServiceImpl<ContractSettlePlanMapper, ContractSettlePlan> implements ContractSettlePlanService {
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ContractService contractService;

    @Override
    public void removeByContractId(Long contractId) {
        LambdaQueryWrapper<ContractSettlePlan> query = Wrappers.lambdaQuery();
        query.eq(ContractSettlePlan::getContractId, contractId);
        this.remove(query);
    }

    @Override
    public ContractSettlePlan getLatestContractSettlePlan(Long contractId) {
        LambdaQueryWrapper<ContractSettlePlan> query = Wrappers.lambdaQuery();
        query.eq(ContractSettlePlan::getContractId, contractId);
        query.orderByDesc(ContractSettlePlan::getId);
        query.last("limit 0, 1");
        return this.getOne(query);
    }

    @Override
    public Long saveOrUpdatePlan(ContractSettlePlan contractSettlePlan) {
        // 保存或者变更方案
        this.saveOrUpdate(contractSettlePlan);
        // 如果在流程中则直接返回，不修改流程状态
        if (contractService.isInProcess(contractSettlePlan.getContractId())) {
            return contractSettlePlan.getId();
        }
        // 变更审批流状态
        contractBaseInfoService.updateContractProcessStatus(ContractProcessStatusEnum.SETTLE_UNCOMIIT.name(), contractSettlePlan.getSettleType(), contractSettlePlan.getContractId());
        return contractSettlePlan.getId();
    }
}
