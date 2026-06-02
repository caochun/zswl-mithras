package cn.zswltech.mithras.service.flow.listener.endhandler;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.flow.core.extension.event.context.ProcessEndContext;
import cn.zswltech.mithras.riskcontrol.opinion.RiskControlOpinionHandleStatus;
import cn.zswltech.mithras.service.flow.dynamicform.risk.opinion.RiskOpinionHandleCheckHandler;
import cn.zswltech.mithras.riskcontrol.opinion.RiskControlOpinionMonitor;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.service.riskcontrol.RiskControlOpinionMonitorService;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.RuntimeService;
import org.springframework.stereotype.Component;

import static cn.zswltech.mithras.service.enums.ProcessModelTypeEnum.*;
import static cn.zswltech.mithras.service.others.SpringContextHolder.getBean;

/**
 * @author yibin
 */
@Slf4j
@Component
public class RiskControlOpinionProcessEndHandler extends AbstractProcessEndHandler {

    @Override
    public void handle(ProcessEndContext endContext) {
        Long id = Long.valueOf(endContext.getBusinessKey());
        log.info("client transfer process end. id:{}, endType:{}, startUserId:{}, processInstanceId:{}",
                id, endContext.getEndType(), endContext.getStartUserId(), endContext.getProcessInstanceId());
        boolean passed = ProcessBusinessStatusEnum.success(endContext.getEndType());
        boolean rejected = endContext.getEndType().intValue() == ProcessBusinessStatusEnum.REJECT.getType().intValue()
                || endContext.getEndType().intValue() == ProcessBusinessStatusEnum.REJECT_ALL.getType().intValue();
        RiskControlOpinionMonitorService riskControlOpinionMonitorService = SpringContextHolder.getBean(RiskControlOpinionMonitorService.class);
        //判断新流程处理还是关闭
        boolean closeFlag = false;
        boolean handleFlay = false;
        if (CharSequenceUtil.equalsAny(endContext.getModelKey(), RiskControlNotPaymentFlow.name(), RiskControlPaymentFlow.name())) {
            Integer variable = (Integer) getBean(RuntimeService.class).getVariable(endContext.getProcessInstanceId(), RiskOpinionHandleCheckHandler.HANDLE_TYPE);
            if (ObjectUtil.isNotEmpty(variable) && variable > 0) {
                handleFlay = true;
            } else {
                closeFlag = true;
            }
        }
        if (CharSequenceUtil.equalsAny(endContext.getModelKey(), RiskControlOpinionHandleFlow.name(), RiskControlOpinionHandleAfterLaunchFlow.name()) || handleFlay) {
            if (passed) {
                // 审批通过
                riskControlOpinionMonitorService
                        .updateById(new RiskControlOpinionMonitor().setHandleStatus(RiskControlOpinionHandleStatus.HANDLED.name()).setId(id));
            } else if (rejected) {
                // 审批拒绝
                riskControlOpinionMonitorService.updateById(new RiskControlOpinionMonitor().setHandleStatus(RiskControlOpinionHandleStatus.REJECTED.name()).setId(id));
            } else {
                riskControlOpinionMonitorService.updateById(new RiskControlOpinionMonitor().setHandleStatus(RiskControlOpinionHandleStatus.PEND_HANDLE.name()).setId(id));
            }
        }

        if (CharSequenceUtil.equalsAny(endContext.getModelKey(), RiskControlOpinionHandleCloseFlow.name(), RiskControlOpinionHandleAfterLaunchCloseFlow.name()) || closeFlag) {
            if (passed) {
                // 审批通过
                riskControlOpinionMonitorService.updateById(new RiskControlOpinionMonitor().setHandleStatus(RiskControlOpinionHandleStatus.CLOSED.name()).setId(id));
            } else if (rejected) {
                // 审批拒绝
                riskControlOpinionMonitorService.updateById(new RiskControlOpinionMonitor().setHandleStatus(RiskControlOpinionHandleStatus.REJECTED.name()).setId(id));
            } else {
                riskControlOpinionMonitorService.updateById(new RiskControlOpinionMonitor().setHandleStatus(RiskControlOpinionHandleStatus.PEND_HANDLE.name()).setId(id));
            }
        }
        //3. 抄送人事部门人员、风控部门人员
       /* UserQuery query = new UserQuery();
        query.setOrgId(riskManagementId);
        List<Long> idList = userService.queryUserSys(query).getContents().stream().map(UserDO::getId).collect(toList());
        query.setOrgId(generalManagementId);
        idList.addAll(userService.queryUserSys(query).getContents().stream().map(UserDO::getId).collect(toList()));
        ExecutionProcessBaseREQ req = new ExecutionProcessBaseREQ();
        req.setProcessInstanceId(processInstanceId);
        req.setCcUserIdList(idList);
        req.setMessage(passed ? "审批通过" : "审批拒绝");
        executionApi.cc(req);

        //立即触发一次定时移交的任务， 防止审批通过时间在正式移交时间之后，这种情况会立即移交
        timedPass();*/
    }

    @Override
    public boolean needHandle(ProcessEndContext endContext) {
        return CharSequenceUtil.equalsAny(endContext.getModelKey(), RiskControlOpinionHandleFlow.name(),
                RiskControlOpinionHandleAfterLaunchFlow.name(), RiskControlOpinionHandleCloseFlow.name(),
                RiskControlOpinionHandleAfterLaunchCloseFlow.name(), RiskControlNotPaymentFlow.name(), RiskControlPaymentFlow.name());
    }
}
