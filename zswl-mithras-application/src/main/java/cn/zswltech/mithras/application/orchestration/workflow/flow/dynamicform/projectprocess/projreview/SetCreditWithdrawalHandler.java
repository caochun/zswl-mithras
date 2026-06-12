package cn.zswltech.mithras.application.orchestration.workflow.flow.dynamicform.projectprocess.projreview;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.flow.core.api.FlowVariableApiService;
import cn.zswltech.flow.core.domain.resp.TaskResp;
import cn.zswltech.flow.core.model.ext.UserTaskExt;
import cn.zswltech.mithras.dto.flow.search.TaskDetailRSP;
import cn.zswltech.mithras.workflow.flow.constant.FlowConstants;
import cn.zswltech.mithras.workflow.enums.FlowDynamicFormEnum;
import cn.zswltech.mithras.workflow.flow.dynamicform.DynamicFormHandler;
import com.github.pagehelper.util.StringUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 表单处理器
 *
 * @author ldhu
 * @date 2026/3/25 11:44 AM
 */
@Component
public class SetCreditWithdrawalHandler implements DynamicFormHandler {
    @Resource
    private FlowVariableApiService flowVariableApiService;

    private static final String isGroupCreditKey = "isGroupCredit"; // 是否集团授信
    private static final String isGroupCreditAllAllow = "isGroupCreditAllAllow"; // 是否集团授信全部符合

    private static final String groupCreditAllowKey = "1";  // 符合
    private static final String groupCreditNotAllowKey = "0"; // 不符合

    @Override
    public void handle(Map<String, Object> formMap, TaskResp taskResp, UserTaskExt userTaskExt) {
        /* 增加风控经理、法务经理审批选项“是否符合集团授信提款条件”  两个节点会办，如果有一个不符合则不符合*/
        Map<String, Object> params = flowVariableApiService.getVariables(taskResp.getProcessInstanceId(),
                ListUtil.of(FlowConstants.GROUP_CREDIT_WITHDRAWAL_LAW, FlowConstants.GROUP_CREDIT_WITHDRAWAL_RISK, isGroupCreditKey));
        Map<String, String> from = (Map) formMap.get("projReview_setCreditWithdrawal");
        if (ObjectUtil.isEmpty(from)) {
            return;
        }

        Boolean isGroupCredit = (Boolean) params.get(isGroupCreditKey);
        /*非集团授信评审  不加工这里的数据*/
        if (!isGroupCredit) {
            return;
        }

        /*从数据库中获取已存储数据*/
        String groupCreditWithdrawalLaw = groupCreditNotAllowKey;
        String groupCreditWithdrawalRisk = groupCreditNotAllowKey;
        if ((Boolean) params.get(FlowConstants.GROUP_CREDIT_WITHDRAWAL_LAW)) {
            groupCreditWithdrawalLaw = groupCreditAllowKey;
        }
        if ((Boolean) params.get(FlowConstants.GROUP_CREDIT_WITHDRAWAL_RISK)) {
            groupCreditWithdrawalRisk = groupCreditAllowKey;
        }

        /*从页面获取最新选择*/
        if (StringUtil.isNotEmpty(from.get(FlowConstants.GROUP_CREDIT_WITHDRAWAL_LAW))) {
            groupCreditWithdrawalLaw = from.get(FlowConstants.GROUP_CREDIT_WITHDRAWAL_LAW);
        }
        if (StringUtil.isNotEmpty(from.get(FlowConstants.GROUP_CREDIT_WITHDRAWAL_RISK))) {
            groupCreditWithdrawalRisk = from.get(FlowConstants.GROUP_CREDIT_WITHDRAWAL_RISK);
        }

        /*分析是否符合*/
        if (groupCreditAllowKey.equals(groupCreditWithdrawalLaw) && groupCreditAllowKey.equals(groupCreditWithdrawalRisk)) {
            params.put(isGroupCreditAllAllow, Boolean.TRUE);
        } else {
            params.put(isGroupCreditAllAllow, Boolean.FALSE);
        }

        /*回存当前选择*/
        if (groupCreditAllowKey.equals(groupCreditWithdrawalLaw)) {
            params.put(FlowConstants.GROUP_CREDIT_WITHDRAWAL_LAW, Boolean.TRUE);
        } else if (groupCreditNotAllowKey.equals(groupCreditWithdrawalLaw)) {
            params.put(FlowConstants.GROUP_CREDIT_WITHDRAWAL_LAW, Boolean.FALSE);
        }
        if (groupCreditAllowKey.equals(groupCreditWithdrawalRisk)) {
            params.put(FlowConstants.GROUP_CREDIT_WITHDRAWAL_RISK, Boolean.TRUE);
        } else if (groupCreditNotAllowKey.equals(groupCreditWithdrawalRisk)) {
            params.put(FlowConstants.GROUP_CREDIT_WITHDRAWAL_RISK, Boolean.FALSE);
        }

        flowVariableApiService.setVariables(taskResp.getProcessInstanceId(), params);
    }

    @Override
    public void collect(TaskDetailRSP rsp) {
        /*表单展示和回显*/
        Map<String, Object> params = flowVariableApiService.getVariables(rsp.getProcessInstanceId(),
                ListUtil.of(FlowConstants.GROUP_CREDIT_WITHDRAWAL_LAW, FlowConstants.GROUP_CREDIT_WITHDRAWAL_RISK, isGroupCreditKey));
        if ((Boolean) params.get(FlowConstants.GROUP_CREDIT_WITHDRAWAL_LAW)) {
            rsp.getDynamicFormData().put(FlowConstants.GROUP_CREDIT_WITHDRAWAL_LAW, groupCreditAllowKey);
        } else {
            rsp.getDynamicFormData().put(FlowConstants.GROUP_CREDIT_WITHDRAWAL_LAW, groupCreditNotAllowKey);
        }
        if ((Boolean) params.get(FlowConstants.GROUP_CREDIT_WITHDRAWAL_RISK)) {
            rsp.getDynamicFormData().put(FlowConstants.GROUP_CREDIT_WITHDRAWAL_RISK, groupCreditAllowKey);
        } else {
            rsp.getDynamicFormData().put(FlowConstants.GROUP_CREDIT_WITHDRAWAL_RISK, groupCreditNotAllowKey);
        }
        rsp.getDynamicFormData().put(isGroupCreditKey, (Boolean) params.get(isGroupCreditKey));
    }

    @Override
    public FlowDynamicFormEnum getType() {
        return FlowDynamicFormEnum.projReview_setCreditWithdrawal;
    }
}
