package cn.zswltech.mithras.service.flow.dynamicform.contract;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.zswltech.flow.core.api.FlowVariableApiService;
import cn.zswltech.flow.core.domain.resp.TaskResp;
import cn.zswltech.flow.core.model.ext.UserTaskExt;
import cn.zswltech.mithras.dto.contract.ContractIdListREQ;
import cn.zswltech.mithras.dto.flow.search.TaskDetailRSP;
import cn.zswltech.mithras.service.enums.FlowDynamicFormEnum;
import cn.zswltech.mithras.service.enums.ProcessVarEnum;
import cn.zswltech.mithras.service.flow.dynamicform.DynamicFormHandler;
import cn.zswltech.mithras.service.mapper.model.contract.ContractPrepayment;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.basedata.BaseDataSpecialDateService;
import cn.zswltech.mithras.service.service.contract.ContractPrepaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;

/**
 * @author dingqi
 * @date 2024/12/7
 * @description
 */
@Slf4j
@Component
public class EarlyRepayStartUserModifyFormHandler implements DynamicFormHandler {
    @Resource
    private FlowVariableApiService flowVariableApiService;
    @Resource
    private ContractPrepaymentService contractPrepaymentService;
    @Resource
    private BaseDataSpecialDateService baseDataSpecialDateService;

    @Override
    public void check(Map<String, Object> formMap, TaskResp taskResp, UserTaskExt userTaskExt) {
        // 取出提交流程时候的提前还款日期
        Map<String, Object> map = flowVariableApiService.getVariables(taskResp.getProcessInstanceId(), ListUtil.of(ProcessVarEnum.contractEarlyRepayOriginalDate.name()));
        Object obj = map.get(ProcessVarEnum.contractEarlyRepayOriginalDate.name());
        if (Objects.isNull(obj)) {
            throw new MithrasException("提交流程时的提前还款日期数据未找到");
        }
        LocalDate original = LocalDateTimeUtil.parseDate(obj.toString(), DatePattern.NORM_DATE_PATTERN);
        // 找到当前最新的提前还款日期
        Long contractId = Long.valueOf(taskResp.getBusinessKey());
        ContractPrepayment contractPrepayment = contractPrepaymentService.getList(new ContractIdListREQ(contractId));
        if (Objects.isNull(contractPrepayment)) {
            throw new MithrasException("提前还款方案数据未找到");
        }
        LocalDate current = contractPrepayment.getApplayRepaymentDate();
        if (Objects.isNull(current)) {
            throw new MithrasException("最新的提前还款日期数据未找到");
        }
        // 计算差值
        long days = baseDataSpecialDateService.calculateWorkDays(original, current);
        if (days > 10) {
            throw new MithrasException("修改后的「提前还款日」已超出流程提交时初次填写的提前还款日10个工作日，请修改「提前还款日」或关闭流程后重新发起！");
        }
    }

    @Override
    public void handle(Map<String, Object> formMap, TaskResp taskResp, UserTaskExt userTaskExt) {

    }

    @Override
    public void collect(TaskDetailRSP rsp) {
        rsp.getDynamicFormData().put(this.getType().name(), null);
    }

    @Override
    public FlowDynamicFormEnum getType() {
        return FlowDynamicFormEnum.contract_early_repay_start_user_modify;
    }
}
