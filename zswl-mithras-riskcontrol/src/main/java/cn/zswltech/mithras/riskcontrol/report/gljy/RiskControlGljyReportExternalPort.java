package cn.zswltech.mithras.riskcontrol.report.gljy;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface RiskControlGljyReportExternalPort {

    List<RiskControlRelatedClientExternal> fetchRelatedClients();

    Map<String, Long> clientIdsByCreditCodes(Set<String> creditCodes);

    String submitRelationTrades(List<RiskControlRelationTradeSubmitItem> trades);
}
