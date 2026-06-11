package cn.zswltech.mithras.application.orchestration.adapter.basedata;

import cn.zswltech.mithras.basedata.job.BaseDataJobMessagePort;
import cn.zswltech.mithras.dto.message.MessageAddREQ;
import cn.zswltech.mithras.message.convert.MessageConver;
import cn.zswltech.mithras.message.enums.notice.MessageTypeEnum;
import cn.zswltech.mithras.message.service.MessageService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class BaseDataJobMessagePortAdapter implements BaseDataJobMessagePort {

    @Resource
    private MessageService messageService;
    @Resource
    private MessageConver messageConver;

    @Override
    public void sendLprRemind(List<Long> userIds, String flowId, int month) {
        MessageAddREQ messageAddREQ = new MessageAddREQ();
        messageAddREQ.setFrom("系统通知");
        messageAddREQ.setTo(userIds);
        messageAddREQ.setPcurl("/baseData/lpr");
        messageAddREQ.setContent("基础数据设置-LPR");
        messageAddREQ.setFlowid(flowId);
        messageAddREQ.setRelation("请至【基础数据设置】维护<" + month + ">月LPR");
        messageAddREQ.setMessageType(MessageTypeEnum.BASE_DATA_LPR_REMIND.name());
        messageService.sendMessage(messageConver.reqToMessage(messageAddREQ));
    }
}
