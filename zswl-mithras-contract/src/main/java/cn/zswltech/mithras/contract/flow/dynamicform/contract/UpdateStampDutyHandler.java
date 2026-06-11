package cn.zswltech.mithras.contract.flow.dynamicform.contract;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.flow.core.domain.resp.TaskResp;
import cn.zswltech.flow.core.model.ext.UserTaskExt;
import cn.zswltech.mithras.dto.flow.form.UpdateStampDutyFormRSP;
import cn.zswltech.mithras.dto.flow.search.TaskDetailRSP;
import cn.zswltech.mithras.workflow.enums.FlowDynamicFormEnum;
import cn.zswltech.mithras.workflow.flow.dynamicform.DynamicFormHandler;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractReceipt;
import cn.zswltech.mithras.contract.core.ContractReceiptService;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author yupengfei
 * @date 2024/5/17 17:31
 */
@Component
public class UpdateStampDutyHandler implements DynamicFormHandler {

    @Resource
    private ContractReceiptService contractReceiptService;

    @Override
    public void handle(Map<String, Object> formMap, TaskResp taskResp, UserTaskExt userTaskExt) {

        String jsonStr = JSONUtil.toJsonStr(formMap.get(this.getType().name()));
        if (StrUtil.isNotBlank(jsonStr)) {
            UpdateStampDutyFormRSP rsp = JSONUtil.toBean(jsonStr, UpdateStampDutyFormRSP.class);
            for (UpdateStampDutyFormRSP.StampDutyData stampDutyData : rsp.getDutyFormRSPList()) {
                contractReceiptService.update(Wrappers.<ContractReceipt>lambdaUpdate()
                        .eq(ContractReceipt::getReceiptCode, stampDutyData.getReceiptCode())
                        .set(Objects.nonNull(stampDutyData.getStampDuty()), ContractReceipt::getStampDuty, stampDutyData.getStampDuty()));
            }
        }
    }

    @Override
    public void collect(TaskDetailRSP rsp) {
        List<UpdateStampDutyFormRSP.StampDutyData> stampDutyDataList = new ArrayList<>();
        Long contractId = Long.parseLong(rsp.getBusinessKey());
        //借据集合
        List<ContractReceipt> receiptList = contractReceiptService.list(Wrappers.<ContractReceipt>lambdaQuery().eq(ContractReceipt::getContractId, contractId));
        if (CollectionUtils.isNotEmpty(receiptList)) {
            stampDutyDataList = receiptList.stream().map(receipt -> UpdateStampDutyFormRSP.StampDutyData.builder()
                    .receiptCode(receipt.getReceiptCode())
                    .stampDuty(receipt.getStampDuty()).build()).collect(Collectors.toList());
        }
        final UpdateStampDutyFormRSP updateStampDutyFormRSP = new UpdateStampDutyFormRSP().setDutyFormRSPList(stampDutyDataList);

        rsp.getDynamicFormData().put(this.getType().name(), updateStampDutyFormRSP);
    }

    @Override
    public FlowDynamicFormEnum getType() {
        return FlowDynamicFormEnum.contract_updateStampDuty;
    }
}
