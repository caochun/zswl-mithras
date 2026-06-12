package cn.zswltech.mithras.application.orchestration.adapter.riskcontrol;

import cn.zswltech.mithras.dto.message.MessageAddREQ;
import cn.zswltech.mithras.dto.message.MessageUrlEnum;
import cn.zswltech.mithras.message.convert.MessageConver;
import cn.zswltech.mithras.message.enums.notice.MessageTypeEnum;
import cn.zswltech.mithras.message.service.MessageService;
import cn.zswltech.mithras.riskcontrol.common.AlertState;
import cn.zswltech.mithras.riskcontrol.metric.RiskControlNotificationPort;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

import static cn.zswltech.mithras.riskcontrol.common.AlertState.WARNING;

@Component
public class RiskControlNotificationPortAdapter implements RiskControlNotificationPort {
    private static final String RISK_MONITOR_MODULE = "RISK_MONITOR";

    @Resource
    private MessageService messageService;
    @Resource
    private MessageConver messageConver;

    @Override
    public void sendIndicatorWarning(List<Long> to, Long strategyId, String metricCode, AlertState alertState) {
        MessageAddREQ addReq = new MessageAddREQ();
        addReq.setTo(to);
        addReq.setMessageType(MessageTypeEnum.INDICATOR_WARNING.name());
        addReq.setNeedOa(true);
        addReq.setNoticeSource(RISK_MONITOR_MODULE);
        addReq.setPcurl(String.format(MessageUrlEnum.INDICATOR_WARNING.pcUrl, strategyId));
        addReq.setFrom("风险监控");
        if (alertState.equals(WARNING)) {
            addReq.setRelation(metricCode + "指标预警");
            addReq.setContent(metricCode);
        } else {
            addReq.setRelation(metricCode + "指标超限");
            addReq.setContent(metricCode);
        }
        messageService.sendMessage(messageConver.reqToMessage(addReq));
    }
}
