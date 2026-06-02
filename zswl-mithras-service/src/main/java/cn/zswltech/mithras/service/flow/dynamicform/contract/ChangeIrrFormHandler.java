package cn.zswltech.mithras.service.flow.dynamicform.contract;

import cn.zswltech.flow.core.domain.resp.TaskResp;
import cn.zswltech.flow.core.model.ext.UserTaskExt;
import cn.zswltech.mithras.dto.contract.ContractIrrRSP;
import cn.zswltech.mithras.dto.flow.search.TaskDetailRSP;
import cn.zswltech.mithras.service.enums.FlowDynamicFormEnum;
import cn.zswltech.mithras.service.flow.dynamicform.DynamicFormHandler;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.contract.ContractService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @description: 合同IRR表单处理器
 * @author: huangping
 * @date: 2026/1/27  16:37
 * @version: 1.0
 */
@Component
public class ChangeIrrFormHandler implements DynamicFormHandler {

    @Resource
    private ContractService contractService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;

    @Override
    public void handle(Map<String, Object> formMap, TaskResp taskResp, UserTaskExt userTaskExt) {

    }

    @Override
    public void collect(TaskDetailRSP rsp) {
        Long contractId = Long.parseLong(rsp.getBusinessKey());
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(contractId);
        ContractIrrRSP  contractIrrRSP = contractService.getLowestIrrAndkAverageIrr(contractId, contractBaseInfo);
        rsp.getDynamicFormData().put(this.getType().name(), contractIrrRSP);
    }

    @Override
    public FlowDynamicFormEnum getType() {
        return FlowDynamicFormEnum.contract_changeIrrForm;
    }
}
