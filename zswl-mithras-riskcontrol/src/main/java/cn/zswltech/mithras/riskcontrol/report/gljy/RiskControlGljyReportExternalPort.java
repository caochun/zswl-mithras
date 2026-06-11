package cn.zswltech.mithras.riskcontrol.report.gljy;

import java.util.List;

public interface RiskControlGljyReportExternalPort {

    List<RiskControlRelatedClientExternal> fetchRelatedClients();

    String submitRelationTrades(List<RiskControlRelationTradeSubmitItem> trades);
}
