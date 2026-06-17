package cn.zswltech.mithras.application.orchestration.adapter.riskcontrol;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.metric.emit.MetricEmitter;
import cn.zswltech.mithras.metric.emit.model.req.relation.trade.RelatedClientListREQ;
import cn.zswltech.mithras.metric.emit.model.req.relation.trade.RelatedClientListRSP;
import cn.zswltech.mithras.metric.emit.model.req.relation.trade.RelationTradeBody;
import cn.zswltech.mithras.metric.emit.model.req.relation.trade.RelationTradeReqBody;
import cn.zswltech.mithras.customer.mapper.client.ClientMapper;
import cn.zswltech.mithras.customer.model.client.Client;
import cn.zswltech.mithras.riskcontrol.report.gljy.RiskControlGljyReportExternalPort;
import cn.zswltech.mithras.riskcontrol.report.gljy.RiskControlRelatedClientExternal;
import cn.zswltech.mithras.riskcontrol.report.gljy.RiskControlRelationTradeSubmitItem;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class RiskControlGljyReportExternalPortAdapter implements RiskControlGljyReportExternalPort {

    @Resource
    private MetricEmitter metricEmitter;
    @Resource
    private ClientMapper clientMapper;

    @Override
    public List<RiskControlRelatedClientExternal> fetchRelatedClients() {
        List<RelatedClientListRSP> clients = metricEmitter.fetchRelatedClientList(new RelatedClientListREQ());
        return BeanUtil.copyToList(clients, RiskControlRelatedClientExternal.class);
    }

    @Override
    public Map<String, Long> clientIdsByCreditCodes(Set<String> creditCodes) {
        if (creditCodes == null || creditCodes.isEmpty()) {
            return Collections.emptyMap();
        }
        return clientMapper.selectList(Wrappers.<Client>lambdaQuery()
                        .in(Client::getUscCode, creditCodes))
                .stream()
                .filter(client -> client.getUscCode() != null && !client.getUscCode().isEmpty())
                .collect(Collectors.toMap(Client::getUscCode, Client::getId, (a, b) -> a));
    }

    @Override
    public String submitRelationTrades(List<RiskControlRelationTradeSubmitItem> trades) {
        RelationTradeReqBody reqBody = new RelationTradeReqBody();
        reqBody.setRelatedTrades(BeanUtil.copyToList(trades, RelationTradeBody.class));
        return metricEmitter.emitRelationTrade(reqBody);
    }
}
