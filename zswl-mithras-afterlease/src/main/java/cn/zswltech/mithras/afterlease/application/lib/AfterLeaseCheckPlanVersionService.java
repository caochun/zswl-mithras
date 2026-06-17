package cn.zswltech.mithras.afterlease.application.lib;

import cn.zswltech.mithras.afterlease.application.AfterLeaseNotificationPort;
import cn.zswltech.mithras.afterlease.application.AfterLeaseWorkdayCalendarPort;
import cn.zswltech.mithras.afterlease.application.AfterLeaseWorkflowPort;
import cn.zswltech.mithras.dto.version.CommonVersionDiffRSP;
import cn.zswltech.mithras.dto.version.CommonVersionListRSP;
import cn.zswltech.mithras.foundation.persistence.dto.ChangeDTO;
import cn.zswltech.mithras.foundation.persistence.model.CommonVersion;
import cn.zswltech.mithras.afterlease.model.NewAfterLeaseCheckPlanBase;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.version.CommonVersionService;
import cn.zswltech.mithras.afterlease.application.lib.handler.AfterLeaseCheckPlanLibAbstractHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * @author dingqi
 * @date 2022/12/15
 * @description
 */
@Component
public class AfterLeaseCheckPlanVersionService extends CommonVersionService<NewAfterLeaseCheckPlanBase> {
    @Autowired
    private List<AfterLeaseCheckPlanLibAbstractHandler> libHandlerList;
    @Resource
    private AfterLeaseWorkflowPort afterLeaseWorkflowPort;
    @Resource
    private AfterLeaseNotificationPort notificationPort;
    @Resource
    private AfterLeaseWorkdayCalendarPort workdayCalendarPort;

    @Override
    public void customFlushData(NewAfterLeaseCheckPlanBase newAfterLeaseCheckPlanBase, String version, boolean needClearLastFlag, Integer versionType) {
        // 处理抄表逻辑
        for (AfterLeaseCheckPlanLibAbstractHandler libHandler : libHandlerList) {
            libHandler.flushData(version, newAfterLeaseCheckPlanBase.getId(), needClearLastFlag, versionType);
        }
    }

    @Override
    public ChangeDTO checkActualChange(Long mainId) {
        throw new MithrasException("暂不支持的功能");
    }

    @Override
    public void customReset(NewAfterLeaseCheckPlanBase newAfterLeaseCheckPlanBase, CommonVersion commonVersion) {
        // 处理抄表逻辑
        for (AfterLeaseCheckPlanLibAbstractHandler libHandler : libHandlerList) {
            libHandler.reset(newAfterLeaseCheckPlanBase.getId(), commonVersion.getVersion());
        }
    }

    @Override
    public CommonVersionDiffRSP doCompare(CommonVersion newVersion, CommonVersion oldVersion) {
        throw new MithrasException("暂不支持的功能");
    }

    @Override
    protected CommonVersionListRSP convertPageRsp(CommonVersion cv, NewAfterLeaseCheckPlanBase baseModel, Map<Long, String> userNameMap) {
        throw new MithrasException("暂不支持的功能");
    }

    @Override
    protected String getBusinessModuleName() {
        return "NEW_AFTER_LEASE_CHECK_PLAN";
    }

    /**
     * 2个工作日未提交提醒
     * @author: luyujie
     * @date: 2025/11/27
     **/
    public void afterLeaseCheckRemind(String id) {
        if (!workdayCalendarPort.isWorkday(LocalDate.now())) {
            return;
        }
        for (AfterLeaseWorkflowPort.ApprovalReminderTask task : afterLeaseWorkflowPort.listReportApprovalReminderTasks()) {
            //针对超过2个工作日的处理人发起消息提醒
            if (workdayCalendarPort.countWorkdayNumber(task.getTaskCreateTime().toLocalDate(), LocalDate.now()) == 3 && task.getAssignee() != null) {
                notificationPort.sendReportApprovalRemind(
                        task.getAssignee(),
                        task.getTaskId(),
                        task.getBusinessKey(),
                        task.getSubModule(),
                        task.getClientName(),
                        task.getModelName(),
                        task.getProcessInstanceId());
            }
        }
    }
}
