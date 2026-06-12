package cn.zswltech.mithras.application.orchestration.adapter.leaseholdproperty;

import cn.zswltech.mithras.dto.message.MessageAddREQ;
import cn.zswltech.mithras.dto.message.MessageUrlEnum;
import cn.zswltech.mithras.leaseholdproperty.job.service.AppraisalWhitelistNotificationPort;
import cn.zswltech.mithras.message.convert.MessageConver;
import cn.zswltech.mithras.message.enums.notice.MessageTypeEnum;
import cn.zswltech.mithras.message.enums.notice.NoticeSourceENUM;
import cn.zswltech.mithras.message.service.MessageService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class AppraisalWhitelistNotificationPortAdapter implements AppraisalWhitelistNotificationPort {

    @Resource
    private MessageService messageService;
    @Resource
    private MessageConver messageConver;

    @Override
    public void sendExpireRemind(Long whitelistId, List<Long> toIds, String relation) {
        MessageAddREQ messageAddREQ = new MessageAddREQ();
        MessageUrlEnum messageUrlEnum = MessageUrlEnum.APPRAISAL_COMPANY_WHITELIST_EXPIRE;
        messageAddREQ.setFrom("系统通知");
        messageAddREQ.setTo(toIds);
        messageAddREQ.setContent(String.valueOf(whitelistId));
        messageAddREQ.setFlowid(String.valueOf(whitelistId));
        messageAddREQ.setRelation(relation);
        messageAddREQ.setNeedOa(Boolean.FALSE);
        messageAddREQ.setNoticeSource(NoticeSourceENUM.APPRAISAL_COMPANY_WHITELIST_EXPIRE.name());
        messageAddREQ.setMessageType(MessageTypeEnum.APPRAISAL_COMPANY_WHITELIST_EXPIRE.name());
        messageAddREQ.setPcurl(String.format(messageUrlEnum.pcUrl, whitelistId));
        messageAddREQ.setAppurl(messageUrlEnum.appUrl);
        messageAddREQ.setBusinessId(String.valueOf(whitelistId));
        messageService.sendMessage(messageConver.reqToTodoMessage(messageAddREQ));
    }
}
