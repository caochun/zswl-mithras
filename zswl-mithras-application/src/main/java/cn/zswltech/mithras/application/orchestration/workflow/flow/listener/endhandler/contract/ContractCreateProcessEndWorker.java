package cn.zswltech.mithras.application.orchestration.workflow.flow.listener.endhandler.contract;

import cn.hutool.core.collection.ListUtil;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractProcessStatusEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.core.evaluation.ContractEvaluationAgencyDraftService;
import cn.zswltech.mithras.financeprojectdistribution.service.impl.FinanceProjectDistributionService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;

/**
 * 合同创建
 *
 * @author wangchuanhao
 * @date 2022/12/15 10:42 AM
 */
@Component
public class ContractCreateProcessEndWorker extends AbstractContractProcessEndWorker {

    @Resource
    private ContractEvaluationAgencyDraftService contractEvaluationAgencyDraftService;
    @Resource
    private FinanceProjectDistributionService financeProjectDistributionService;

    @Override
    public List<ProcessModelTypeEnum> handleModelTypeList() {
        return ListUtil.toList(ProcessModelTypeEnum.ContractCreateFlow);
    }

    @Override
    public void customProcessPass(String modelKey, Long contractId, Integer endType, Long startUserId, String processInstanceId) {
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(contractId);
        // 填充合同生效时间
        ContractBaseInfo toUpdate = new ContractBaseInfo();
        toUpdate.setId(contractBaseInfo.getId());
        toUpdate.setPaymentPlanDate(LocalDate.now());
        contractBaseInfoService.updateById(toUpdate);
        // 抄送租赁物的评估机构到生效区
        contractEvaluationAgencyDraftService.copyDraft2Effect(contractId);
        /*生成项目利润分配待办*/
        financeProjectDistributionService.initProcessPrepare(contractId);
    }

    @Override
    public ContractStatus getContractStatus(String modelKey, boolean processPass) {
        return processPass ? ContractStatus.TAKE_EFFECT : null;
    }

    @Override
    public ContractProcessStatusEnum getContractProcessStatus(String modelKey, boolean processPass) {
        return processPass ? ContractProcessStatusEnum.NEW_PASS : ContractProcessStatusEnum.NEW_CANCEL;
    }

}
