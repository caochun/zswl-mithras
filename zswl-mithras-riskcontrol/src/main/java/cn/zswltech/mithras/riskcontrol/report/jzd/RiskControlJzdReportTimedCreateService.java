package cn.zswltech.mithras.riskcontrol.report.jzd;

import java.time.LocalDate;

public interface RiskControlJzdReportTimedCreateService {

    void generateQuarterlyData(LocalDate dataMonth);
}
