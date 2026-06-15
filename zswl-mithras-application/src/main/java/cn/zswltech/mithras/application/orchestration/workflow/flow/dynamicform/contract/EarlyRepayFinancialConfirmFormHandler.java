package cn.zswltech.mithras.application.orchestration.workflow.flow.dynamicform.contract;

import cn.zswltech.flow.core.api.FlowVariableApiService;
import cn.zswltech.flow.core.domain.resp.TaskResp;
import cn.zswltech.flow.core.model.ext.UserTaskExt;
import cn.zswltech.mithras.dto.flow.search.TaskDetailRSP;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.workflow.enums.FlowDynamicFormEnum;
import cn.zswltech.mithras.workflow.enums.ProcessVarEnum;
import cn.zswltech.mithras.workflow.flow.dynamicform.DynamicFormHandler;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author dingqi
 * @date 2024/12/7
 * @description
 */
@Slf4j
@Component
public class EarlyRepayFinancialConfirmFormHandler implements DynamicFormHandler {
    @Resource
    private FlowVariableApiService flowVariableApiService;

    @Override
    public void handle(Map<String, Object> formMap, TaskResp taskResp, UserTaskExt userTaskExt) {
        FormData formData = JSONObject.parseObject(JSON.toJSONString(formMap.get(getType().name())), FormData.class);
        if (Objects.isNull(formData) || Objects.isNull(formData.getIsPass())) {
            throw new MithrasException("是否通过不能为空");
        }
        Map<String, Object> map = new HashMap<>();
        if (Objects.equals(formData.getIsPass(), YesOrNoNumberEnum.YES.getCode())) {
            map.put(ProcessVarEnum.contractEarlyRepayFinancialConfirmPass.name(), Boolean.TRUE);
        } else {
            map.put(ProcessVarEnum.contractEarlyRepayFinancialConfirmPass.name(), Boolean.FALSE);
        }
        flowVariableApiService.setVariables(taskResp.getProcessInstanceId(), map);
    }

    @Override
    public void collect(TaskDetailRSP rsp) {
        rsp.getDynamicFormData().put(this.getType().name(), new FormData());
    }

    @Override
    public FlowDynamicFormEnum getType() {
        return FlowDynamicFormEnum.contract_early_repay_financial_confirm;
    }

    @Data
    private static class FormData {
        private Integer isPass;
    }
}
