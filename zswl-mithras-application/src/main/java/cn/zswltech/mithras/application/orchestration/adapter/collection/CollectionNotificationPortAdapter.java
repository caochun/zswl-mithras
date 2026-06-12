package cn.zswltech.mithras.application.orchestration.adapter.collection;

import cn.hutool.core.util.IdUtil;
import cn.zswltech.mithras.collection.application.job.CollectionNotificationPort;
import cn.zswltech.mithras.dto.message.MessageAddREQ;
import cn.zswltech.mithras.message.convert.MessageConver;
import cn.zswltech.mithras.message.enums.notice.MessageTypeEnum;
import cn.zswltech.mithras.message.enums.notice.NoticeSourceENUM;
import cn.zswltech.mithras.message.service.MessageService;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

@Component
public class CollectionNotificationPortAdapter implements CollectionNotificationPort {

    @Resource
    private MessageService messageService;
    @Resource
    private MessageConver messageConver;

    @Override
    public void sendRentDueRemind(List<Long> toIds, Long collectionId, String code, String relation) {
        MessageAddREQ addRequest = new MessageAddREQ();
        addRequest.setFrom("系统通知");
        addRequest.setTo(toIds);
        addRequest.setPcurl(StringUtils.format("/cpm/collectionWriteOff/detail/%s", collectionId));
        addRequest.setContent(code);
        addRequest.setFlowid(IdUtil.getSnowflakeNextIdStr());
        addRequest.setNeedOa(false);
        addRequest.setRelation(relation);
        addRequest.setMessageType(MessageTypeEnum.RENT.name());
        addRequest.setNoticeSource(NoticeSourceENUM.RENT.name());
        messageService.sendMessage(messageConver.reqToMessage(addRequest));
    }

    @Override
    public void sendRentRepayOverdueRemind(Long toId, String relation) {
        MessageAddREQ addRequest = new MessageAddREQ();
        addRequest.setFrom("系统通知");
        addRequest.setTo(Collections.singletonList(toId));
        addRequest.setPcurl("/cpm/collectionWriteOff");
        addRequest.setContent("收款核销提醒");
        addRequest.setFlowid(IdUtil.getSnowflakeNextIdStr());
        addRequest.setRelation(relation);
        addRequest.setMessageType(MessageTypeEnum.COLLECTION_NOTICE.name());
        messageService.sendMessage(messageConver.reqToMessage(addRequest));
    }
}
