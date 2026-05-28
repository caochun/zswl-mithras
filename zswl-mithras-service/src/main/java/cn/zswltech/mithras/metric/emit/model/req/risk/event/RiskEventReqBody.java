package cn.zswltech.mithras.metric.emit.model.req.risk.event;

import lombok.Data;

import java.util.List;

/**
 * @author yibin
 */
@Data
public class RiskEventReqBody {
    private List<RiskEventReqSingleEvent> eventList;
}
