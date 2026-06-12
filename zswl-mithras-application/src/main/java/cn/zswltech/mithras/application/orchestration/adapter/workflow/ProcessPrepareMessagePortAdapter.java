package cn.zswltech.mithras.application.orchestration.adapter.workflow;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.dto.message.MessageAddREQ;
import cn.zswltech.mithras.message.convert.MessageConver;
import cn.zswltech.mithras.dto.message.MessageUrlEnum;
import cn.zswltech.mithras.message.enums.notice.MessageTypeEnum;
import cn.zswltech.mithras.message.enums.notice.NoticeSourceENUM;
import cn.zswltech.mithras.message.model.MessageModel;
import cn.zswltech.mithras.message.model.NoticeMessageBody;
import cn.zswltech.mithras.message.service.MessageService;
import cn.zswltech.mithras.workflow.process.prepare.ProcessPrepareMessagePort;
import cn.zswltech.mithras.workflow.model.CommonProcessPrepare;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Component
public class ProcessPrepareMessagePortAdapter implements ProcessPrepareMessagePort {

    @Resource
    private MessageService messageService;
    @Resource
    private MessageConver messageConvert;

    @Override
    public void noticeMessage(CommonProcessPrepare prepare) {
        if (ObjectUtil.isEmpty(prepare)) {
            return;
        }
        MessageAddREQ messageAddREQ = new MessageAddREQ();
        messageAddREQ.setFrom("系统通知");
        messageAddREQ.setMessageType(MessageTypeEnum.PROCESS_PREPARE.name());
        messageAddREQ.setContent(prepare.getBusinessId());
        messageAddREQ.setPcurl(String.format(MessageUrlEnum.PROCESS_PREPARE.pcUrl, prepare.getId()));
        List<Long> to = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(prepare.getCurrentAssignee())) {
            to.addAll(JSONUtil.toList(prepare.getCurrentAssignee(), Long.class));
        }
        messageAddREQ.setTo(to);
        messageAddREQ.setNeedOa(false);
        messageAddREQ.setNeedQa(false);
        messageAddREQ.setNoticeSource(NoticeSourceENUM.PROCESS_PREPARE.name());
        MessageModel messageModel = messageConvert.reqToMessage(messageAddREQ);
        NoticeMessageBody body = (NoticeMessageBody) messageModel.getBodie();
        body.setTitle(prepare.getFormName());
        messageService.sendMessage(messageModel);
    }
}
