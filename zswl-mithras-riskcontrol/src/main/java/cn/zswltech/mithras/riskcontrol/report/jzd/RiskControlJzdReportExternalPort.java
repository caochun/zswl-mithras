package cn.zswltech.mithras.riskcontrol.report.jzd;

import java.util.List;

public interface RiskControlJzdReportExternalPort {

    String submitConcentration(String timePoint, List<RiskControlJzdReportSubmitItem> items);
}
