package cn.zswltech.mithras.application.orchestration.adapter.archives;

import cn.zswltech.mithras.archives.application.ArchivesNotificationPort;
import cn.zswltech.mithras.dto.message.MessageAddREQ;
import cn.zswltech.mithras.message.convert.MessageConver;
import cn.zswltech.mithras.message.enums.MessageUrlEnum;
import cn.zswltech.mithras.message.enums.notice.MessageTypeEnum;
import cn.zswltech.mithras.message.enums.notice.NoticeSourceENUM;
import cn.zswltech.mithras.message.service.MessageService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Component
public class ArchivesNotificationPortAdapter implements ArchivesNotificationPort {

    @Resource
    private MessageService messageService;
    @Resource
    private MessageConver messageConvert;

    @Override
    public void sendRemind(Long archivesId, String projName, Long projSponsorUserId) {
        MessageAddREQ message = new MessageAddREQ();
        message.setFrom("系统通知");
        List<Long> to = new ArrayList<>();
        if (projSponsorUserId != null) {
            to.add(projSponsorUserId);
        }
        message.setTo(to);
        message.setRelation(projName);
        message.setContent(projName);
        message.setNeedOa(false);
        message.setNoticeSource(NoticeSourceENUM.ARCHIVES.name());
        message.setMessageType(MessageTypeEnum.ARCHIVES.name());
        message.setPcurl(String.format(MessageUrlEnum.ARCHIVES.pcUrl, archivesId));
        message.setBusinessId(String.valueOf(archivesId));
        messageService.sendMessage(messageConvert.reqToMessage(message));
    }
}
