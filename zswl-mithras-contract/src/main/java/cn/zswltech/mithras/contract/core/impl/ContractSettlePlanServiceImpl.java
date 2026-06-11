package cn.zswltech.mithras.contract.core.impl;

import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import cn.zswltech.mithras.contract.core.ContractSettlePlanService;
import cn.zswltech.mithras.contract.enums.contract.ContractProcessStatusEnum;
import cn.zswltech.mithras.contract.mapper.contract.ContractSettlePlanMapper;
import cn.zswltech.mithras.contract.model.contract.ContractSettlePlan;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author dingqi
 * @date 2022/8/24
 * @description
 */
@Service
public class ContractSettlePlanServiceImpl extends ServiceImpl<ContractSettlePlanMapper, ContractSettlePlan> implements ContractSettlePlanService {
    private static final List<String> CONTRACT_PROCESS_MODEL_KEYS = Arrays.asList(
            ProcessModelTypeEnum.ContractCreateFlow.name(),
            ProcessModelTypeEnum.ContractModifyFlow.name(),
            ProcessModelTypeEnum.ContractEarlySettleFlow.name(),
            ProcessModelTypeEnum.ContractNormalSettleFlow.name(),
            ProcessModelTypeEnum.ContractLPRChangeFlow.name(),
            ProcessModelTypeEnum.ContractExtensionFlow.name(),
            ProcessModelTypeEnum.ContractEarlyRepayFlow.name(),
            ProcessModelTypeEnum.ContractStartRentFlow.name(),
            ProcessModelTypeEnum.ContractAddNewReceiptFlow.name(),
            ProcessModelTypeEnum.ContractChangeRepayPlanFlow.name(),
            ProcessModelTypeEnum.ContractStartRentAutoFlow.name(),
            ProcessModelTypeEnum.ContractAddNewReceiptAutoFlow.name(),
            ProcessModelTypeEnum.ContractEarlySettleConfirmFlow.name()
    );

    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private FlowTaskApiService taskApiService;

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
        this.saveOrUpdate(contractSettlePlan);
        if (isInProcess(contractSettlePlan.getContractId())) {
            return contractSettlePlan.getId();
        }
        contractBaseInfoService.updateContractProcessStatus(ContractProcessStatusEnum.SETTLE_UNCOMIIT.name(), contractSettlePlan.getSettleType(), contractSettlePlan.getContractId());
        return contractSettlePlan.getId();
    }

    private boolean isInProcess(Long contractId) {
        ProcessPageReq req = new ProcessPageReq();
        req.setBusinessKey(String.valueOf(contractId));
        req.setPageIndex(1);
        req.setPageSize(1);
        req.setModelKeyList(CONTRACT_PROCESS_MODEL_KEYS);
        req.setProcessStatusList(Collections.singletonList(ProcessBusinessStatusEnum.RUNNING.getType()));
        return Objects.nonNull(taskApiService.queryProcess(req).getContents()
                .stream().findFirst().orElse(null));
    }
}
