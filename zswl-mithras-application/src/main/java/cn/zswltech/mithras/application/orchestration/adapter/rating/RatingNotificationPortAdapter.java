package cn.zswltech.mithras.application.orchestration.adapter.rating;

import cn.zswltech.mithras.dto.message.MessageAddREQ;
import cn.zswltech.mithras.message.convert.MessageConver;
import cn.zswltech.mithras.message.enums.notice.MessageTypeEnum;
import cn.zswltech.mithras.message.enums.notice.NoticeSourceENUM;
import cn.zswltech.mithras.message.service.MessageService;
import cn.zswltech.mithras.rating.application.RatingNotificationPort;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;

@Component
public class RatingNotificationPortAdapter implements RatingNotificationPort {

    @Resource
    private MessageService messageService;
    @Resource
    private MessageConver messageConver;

    @Override
    public void sendRatingAmountOverdueRemind(
            Long toId,
            Long ratingAmountId,
            String projectName,
            String clientName,
            Long projReviewId,
            String bizType) {
        MessageAddREQ messageAddREQ = new MessageAddREQ();
        messageAddREQ.setFrom("系统通知");
        messageAddREQ.setTo(Collections.singletonList(toId));
        messageAddREQ.setFlowid(String.valueOf(ratingAmountId));
        messageAddREQ.setContent(String.format("%s项目评估主体为%s的债项评级将在一个月后过期,请及时更新", projectName, clientName));
        messageAddREQ.setNeedOa(false);
        messageAddREQ.setRelation(String.format("%s的债项评级将在一个月后过期", clientName));
        messageAddREQ.setNoticeSource(NoticeSourceENUM.RATING_AMOUNT_UPDATE.name());
        messageAddREQ.setMessageType(MessageTypeEnum.RATING_AMOUNT_OVER_DUE.name());
        if (projReviewId != null) {
            messageAddREQ.setPcurl(String.format("/project/review/detail/%s?typeId=review&bizType=%s&modal=amount", projReviewId, bizType));
        }
        messageAddREQ.setBusinessId(String.valueOf(ratingAmountId));
        messageService.sendMessage(messageConver.reqToMessage(messageAddREQ));
    }

    @Override
    public void sendRatingClientOverdueRemind(Long toId, Long ratingClientId, String clientName) {
        MessageAddREQ messageAddREQ = new MessageAddREQ();
        messageAddREQ.setFrom("系统通知");
        messageAddREQ.setTo(Collections.singletonList(toId));
        messageAddREQ.setFlowid(String.valueOf(ratingClientId));
        messageAddREQ.setContent(String.format("%s的客户评级将在一个月后过期,请及时更新", clientName));
        messageAddREQ.setNeedOa(false);
        messageAddREQ.setRelation(String.format("%s的客户评级将在一个月后过期", clientName));
        messageAddREQ.setNoticeSource(NoticeSourceENUM.RATING_CLIENT_UPDATE.name());
        messageAddREQ.setMessageType(MessageTypeEnum.RATING_CLIENT_OVER_DUE.name());
        messageAddREQ.setPcurl(String.format("/customer/customerRat?search={\"clientName\":\"%s\"}", clientName));
        messageAddREQ.setBusinessId(String.valueOf(ratingClientId));
        messageService.sendMessage(messageConver.reqToMessage(messageAddREQ));
    }
}
