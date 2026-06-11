package cn.zswltech.mithras.metric.adapter.riskcontrol;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.metric.emit.MetricEmitter;
import cn.zswltech.mithras.metric.emit.model.req.relation.trade.RelatedClientListREQ;
import cn.zswltech.mithras.metric.emit.model.req.relation.trade.RelatedClientListRSP;
import cn.zswltech.mithras.metric.emit.model.req.relation.trade.RelationTradeBody;
import cn.zswltech.mithras.metric.emit.model.req.relation.trade.RelationTradeReqBody;
import cn.zswltech.mithras.riskcontrol.report.gljy.RiskControlGljyReportExternalPort;
import cn.zswltech.mithras.riskcontrol.report.gljy.RiskControlRelatedClientExternal;
import cn.zswltech.mithras.riskcontrol.report.gljy.RiskControlRelationTradeSubmitItem;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class RiskControlGljyReportExternalPortAdapter implements RiskControlGljyReportExternalPort {

    @Resource
    private MetricEmitter metricEmitter;

    @Override
    public List<RiskControlRelatedClientExternal> fetchRelatedClients() {
        List<RelatedClientListRSP> clients = metricEmitter.fetchRelatedClientList(new RelatedClientListREQ());
        return BeanUtil.copyToList(clients, RiskControlRelatedClientExternal.class);
    }

    @Override
    public String submitRelationTrades(List<RiskControlRelationTradeSubmitItem> trades) {
        RelationTradeReqBody reqBody = new RelationTradeReqBody();
        reqBody.setRelatedTrades(BeanUtil.copyToList(trades, RelationTradeBody.class));
        return metricEmitter.emitRelationTrade(reqBody);
    }
}
