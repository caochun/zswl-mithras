package cn.zswltech.mithras.service.convert.flow;

import cn.hutool.core.util.StrUtil;
import cn.zswltech.flow.core.domain.req.execution.*;
import cn.zswltech.flow.core.enums.ApprovalButtonTypeEnum;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.dto.flow.execution.*;
import cn.zswltech.mithras.workflow.application.flow.constant.FlowConstants;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 转换
 *
 * @author wangchuanhao
 * @date 2022/8/2 11:33 PM
 */
@Component
public class FlowExecutionConvert {

    public ExecutionPassReq passREQ2FlowReq(ExecutionPassREQ req) {
        ExecutionPassReq executionPassReq = new ExecutionPassReq();
        executionPassReq.setHandlerId(String.valueOf(AccountUtil.getLoginInfo().getId()));
        executionPassReq.setTaskId(req.getTaskId());
        executionPassReq.setMessage(req.getMessage());
        executionPassReq.setCheckAssignee(true);
        if (CollectionUtils.isNotEmpty(req.getCcUserIdList())) {
            executionPassReq.setCcUserIdList(req.getCcUserIdList().stream().map(String::valueOf).collect(Collectors.toList()));
        }
        executionPassReq.setButtonKey(req.getButtonKey());
        return executionPassReq;
    }

    public ExecutionTaskBaseReq taskBaseREQ2FlowReq(ExecutionTaskBaseREQ req) {
        ExecutionTaskBaseReq executionTaskBaseReq = new ExecutionTaskBaseReq();
        executionTaskBaseReq.setHandlerId(String.valueOf(AccountUtil.getLoginInfo().getId()));
        executionTaskBaseReq.setTaskId(req.getTaskId());
        executionTaskBaseReq.setMessage(req.getMessage());
        executionTaskBaseReq.setCheckAssignee(true);
        if (CollectionUtils.isNotEmpty(req.getCcUserIdList())) {
            executionTaskBaseReq.setCcUserIdList(req.getCcUserIdList().stream().map(String::valueOf).collect(Collectors.toList()));
        }
        return executionTaskBaseReq;
    }

    public ExecutionBackToStepReq backToStartUserREQ2FlowReq(ExecutionBackToStartUserREQ req) {
        ExecutionBackToStepReq executionBackToStepReq = new ExecutionBackToStepReq();
        executionBackToStepReq.setTaskActivityId(FlowConstants.START_USER_TASK);
        executionBackToStepReq.setJumpToSourceFlag(Objects.equals(2, req.getBackType()) ? 1 : 0);
        executionBackToStepReq.setHandlerId(String.valueOf(AccountUtil.getLoginInfo().getId()));
        executionBackToStepReq.setTaskId(req.getTaskId());
        executionBackToStepReq.setMessage(req.getMessage());
        executionBackToStepReq.setCheckAssignee(true);
        if (CollectionUtils.isNotEmpty(req.getCcUserIdList())) {
            executionBackToStepReq.setCcUserIdList(req.getCcUserIdList().stream().map(String::valueOf).collect(Collectors.toList()));
        }
        executionBackToStepReq.setButtonKey(StrUtil.isBlank(req.getButtonKey()) ? ApprovalButtonTypeEnum.BACK_TO_START_USER.name() : req.getButtonKey());
        return executionBackToStepReq;

    }

    public ExecutionBackToStepReq backToStepREQ2FlowReq(ExecutionBackToStepREQ req) {
        ExecutionBackToStepReq executionBackToStepReq = new ExecutionBackToStepReq();
        executionBackToStepReq.setTaskActivityId(req.getActivityId());
        // 默认直达本节点
        executionBackToStepReq.setJumpToSourceFlag(Objects.equals(2, req.getBackType()) ? 1 : 0);
        executionBackToStepReq.setHandlerId(String.valueOf(AccountUtil.getLoginInfo().getId()));
        executionBackToStepReq.setTaskId(req.getTaskId());
        executionBackToStepReq.setMessage(req.getMessage());
        executionBackToStepReq.setCheckAssignee(true);
        if (CollectionUtils.isNotEmpty(req.getCcUserIdList())) {
            executionBackToStepReq.setCcUserIdList(req.getCcUserIdList().stream().map(String::valueOf).collect(Collectors.toList()));
        }
        executionBackToStepReq.setButtonKey(req.getButtonKey());
        return executionBackToStepReq;

    }

    public ExecutionWithdrawProcessReq processBaseREQ2WithdrawReq(ExecutionProcessBaseREQ req) {
        ExecutionWithdrawProcessReq executionWithdrawProcessReq = new ExecutionWithdrawProcessReq();
        executionWithdrawProcessReq.setJumpToSourceFlag(0);
        executionWithdrawProcessReq.setHandlerId(String.valueOf(AccountUtil.getLoginInfo().getId()));
        executionWithdrawProcessReq.setProcessInstanceId(req.getProcessInstanceId());
        executionWithdrawProcessReq.setMessage(req.getMessage());
        if (CollectionUtils.isNotEmpty(req.getCcUserIdList())) {
            executionWithdrawProcessReq.setCcUserIdList(req.getCcUserIdList().stream().map(String::valueOf).collect(Collectors.toList()));
        }
        return executionWithdrawProcessReq;
    }

    public ExecutionProcessBaseReq processBaseREQ2FlowReq(ExecutionProcessBaseREQ req) {
        ExecutionProcessBaseReq executionProcessBaseReq = new ExecutionProcessBaseReq();
        if (AccountUtil.getLoginInfo() != null) {
            executionProcessBaseReq.setHandlerId(String.valueOf(AccountUtil.getLoginInfo().getId()));
        } else {
            executionProcessBaseReq.setHandlerId("3");
        }
        executionProcessBaseReq.setProcessInstanceId(req.getProcessInstanceId());
        executionProcessBaseReq.setMessage(req.getMessage());
        if (CollectionUtils.isNotEmpty(req.getCcUserIdList())) {
            executionProcessBaseReq.setCcUserIdList(req.getCcUserIdList().stream().map(String::valueOf).collect(Collectors.toList()));
        }
        return executionProcessBaseReq;

    }

    public ExecutionBeforeAddSignReq collaborateREQ2FlowReq(ExecutionCollaborateREQ req) {
        ExecutionBeforeAddSignReq executionBeforeAddSignReq = new ExecutionBeforeAddSignReq();
        executionBeforeAddSignReq.setAddSignUserIdList(Arrays.asList(String.valueOf(req.getCollaborateUserId())));
        executionBeforeAddSignReq.setHandlerId(String.valueOf(AccountUtil.getLoginInfo().getId()));
        executionBeforeAddSignReq.setTaskId(req.getTaskId());
        executionBeforeAddSignReq.setMessage(req.getMessage());
        executionBeforeAddSignReq.setCheckAssignee(true);
        if (CollectionUtils.isNotEmpty(req.getCcUserIdList())) {
            executionBeforeAddSignReq.setCcUserIdList(req.getCcUserIdList().stream().map(String::valueOf).collect(Collectors.toList()));
        }
        return executionBeforeAddSignReq;

    }

    public ExecutionTransferReq transferREQ2FlowReq(ExecutionTransferREQ req) {
        ExecutionTransferReq executionTransferReq = new ExecutionTransferReq();
        executionTransferReq.setEmployee(String.valueOf(req.getEmployeeId()));
        executionTransferReq.setTaskActivityId(req.getTaskActivityId());
        executionTransferReq.setHandlerId(String.valueOf(AccountUtil.getLoginInfo().getId()));
        executionTransferReq.setProcessInstanceId(req.getProcessInstanceId());
        executionTransferReq.setMessage(req.getMessage());
        if (CollectionUtils.isNotEmpty(req.getCcUserIdList())) {
            executionTransferReq.setCcUserIdList(req.getCcUserIdList().stream().map(String::valueOf).collect(Collectors.toList()));
        }
        return executionTransferReq;
    }

    public ExecutionJumpReq jumpREQ2FlowReq(ExecutionJumpREQ req) {
        ExecutionJumpReq executionJumpReq = new ExecutionJumpReq();
        executionJumpReq.setTaskActivityId(req.getActivityId());
        executionJumpReq.setHandlerId(String.valueOf(AccountUtil.getLoginInfo().getId()));
        executionJumpReq.setProcessInstanceId(req.getProcessInstanceId());
        executionJumpReq.setMessage(req.getMessage());
        if (CollectionUtils.isNotEmpty(req.getCcUserIdList())) {
            executionJumpReq.setCcUserIdList(req.getCcUserIdList().stream().map(String::valueOf).collect(Collectors.toList()));
        }
        return executionJumpReq;

    }

    public ExecutionRandomReturnReq randomReturnREQ2FlowReq(ExecutionRandomReturnREQ req) {
        ExecutionRandomReturnReq executionRandomReturnReq = new ExecutionRandomReturnReq();
        executionRandomReturnReq.setTaskActivityId(req.getActivityId());
        executionRandomReturnReq.setJumpToSourceFlag(Objects.equals(2, req.getBackType()) ? 1 : 0);
        executionRandomReturnReq.setHandlerId(String.valueOf(AccountUtil.getLoginInfo().getId()));
        executionRandomReturnReq.setProcessInstanceId(req.getProcessInstanceId());
        executionRandomReturnReq.setMessage(req.getMessage());
        if (CollectionUtils.isNotEmpty(req.getCcUserIdList())) {
            executionRandomReturnReq.setCcUserIdList(req.getCcUserIdList().stream().map(String::valueOf).collect(Collectors.toList()));
        }
        executionRandomReturnReq.setTaskId(req.getTaskId());
        return executionRandomReturnReq;

    }
}
