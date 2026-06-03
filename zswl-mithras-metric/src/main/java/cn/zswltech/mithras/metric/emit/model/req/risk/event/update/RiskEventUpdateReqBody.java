package cn.zswltech.mithras.metric.emit.model.req.risk.event.update;

import lombok.Data;

import java.util.List;


@Data
public class RiskEventUpdateReqBody {
    private List<RiskEventUpdateReqSingleEvent> eventList;
}