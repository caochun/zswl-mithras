package cn.zswltech.mithras.application.orchestration.adapter.afterlease;

import cn.zswltech.mithras.afterlease.application.AfterLeaseNotificationPort;
import cn.zswltech.mithras.dto.message.MessageAddREQ;
import cn.zswltech.mithras.dto.message.MessageUrlEnum;
import cn.zswltech.mithras.message.convert.MessageConver;
import cn.zswltech.mithras.message.enums.notice.MessageTypeEnum;
import cn.zswltech.mithras.message.enums.notice.NoticeSourceENUM;
import cn.zswltech.mithras.message.service.MessageService;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;

@Component
public class AfterLeaseNotificationPortAdapter implements AfterLeaseNotificationPort {

    @Resource
    private MessageService messageService;
    @Resource
    private MessageConver messageConver;

    @Override
    public void sendCheckPlanTimeoutRemind(Long toId, Long planClientId, String relation) {
        MessageAddREQ messageAddREQ = new MessageAddREQ();
        messageAddREQ.setFrom("系统提醒");
        messageAddREQ.setFlowid(String.valueOf(planClientId));
        messageAddREQ.setRelation(relation);
        messageAddREQ.setNeedOa(false);
        messageAddREQ.setContent(String.valueOf(planClientId));
        messageAddREQ.setNoticeSource("催办通知");
        messageAddREQ.setMessageType(MessageTypeEnum.CLIENT_PLAN_CHECK_SPONSOR.name());
        messageAddREQ.setPcurl(String.format(MessageUrlEnum.CLIENT_PLAN_CHECK.pcUrl, planClientId));
        messageAddREQ.setAppurl(MessageUrlEnum.CLIENT_PLAN_CHECK.appUrl);
        messageAddREQ.setTo(Collections.singletonList(toId));
        messageService.sendMessage(messageConver.reqToMessage(messageAddREQ));
    }

    @Override
    public void sendReportApprovalRemind(
            Long toId,
            String taskId,
            String businessKey,
            String subModule,
            String clientName,
            String modelName,
            String processInstanceId) {
        MessageAddREQ messageAddREQ = new MessageAddREQ();
        messageAddREQ.setFrom("系统通知");
        messageAddREQ.setTo(Collections.singletonList(toId));
        messageAddREQ.setFlowid(taskId);
        messageAddREQ.setRelation("【" + clientName + "】的租后检查报告审批流程" + modelName);
        messageAddREQ.setNeedOa(false);
        messageAddREQ.setNoticeSource(NoticeSourceENUM.APPROVAL_PROCESS.name());
        messageAddREQ.setMessageType(MessageTypeEnum.UNDER_APPROVAL.name());
        MessageUrlEnum noticeContextEnum = MessageUrlEnum.NOTICE_CONTEXT;
        messageAddREQ.setAppurl(StringUtils.format(noticeContextEnum.appUrl, taskId));
        messageAddREQ.setPcurl(StringUtils.format(noticeContextEnum.pcUrl, taskId, businessKey, subModule));
        messageAddREQ.setTaskId(taskId);
        messageAddREQ.setContent(processInstanceId);
        messageService.sendMessageAsync(messageConver.reqToTodoMessage(messageAddREQ));
    }
}
