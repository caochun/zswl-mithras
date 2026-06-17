package cn.zswltech.mithras.application.orchestration.adapter.report;

import cn.zswltech.mithras.api.report.ReportNotificationPort;
import cn.zswltech.mithras.dto.message.MessageAddREQ;
import cn.zswltech.mithras.dto.message.MessageUrlEnum;
import cn.zswltech.mithras.message.convert.MessageConver;
import cn.zswltech.mithras.message.enums.notice.MessageTypeEnum;
import cn.zswltech.mithras.message.enums.notice.NoticeSourceENUM;
import cn.zswltech.mithras.message.service.MessageService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class ReportNotificationPortAdapter implements ReportNotificationPort {

    @Resource
    private MessageService messageService;
    @Resource
    private MessageConver messageConver;

    @Override
    public void sendCreditReportDataChange(List<Long> receivers, String processInstanceId) {
        MessageAddREQ addREQ = new MessageAddREQ();
        addREQ.setTo(receivers);
        addREQ.setMessageType(MessageTypeEnum.CREDIT_REPORT_DATA_CHANGE.name());
        addREQ.setNeedOa(true);
        addREQ.setNoticeSource(NoticeSourceENUM.CREDIT_REPORT.name());
        addREQ.setPcurl(String.format(MessageUrlEnum.CREDIT_REPORT.pcUrl));
        addREQ.setFrom("系统通知");
        addREQ.setRelation(String.format("<%s>审批流中的征信数据发生变动，请至【待报送】查看", processInstanceId));
        addREQ.setContent(processInstanceId);
        messageService.sendMessage(messageConver.reqToMessage(addREQ));
    }
}
