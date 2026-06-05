package cn.zswltech.mithras.riskcontrol.report.jzd;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.riskcontrol.model.JzdReportAddREQ;
import cn.zswltech.mithras.api.riskcontrol.model.JzdReportListREQ;
import cn.zswltech.mithras.api.riskcontrol.model.JzdReportListRSP;
import cn.zswltech.mithras.api.riskcontrol.model.JzdReportModifyREQ;
import cn.zswltech.mithras.api.riskcontrol.model.JzdReportRemoveREQ;
import cn.zswltech.mithras.api.riskcontrol.model.JzdReportSubmitREQ;

public interface RiskControlJzdReportApplicationService {

    void submit(JzdReportSubmitREQ req);

    void addManually(JzdReportAddREQ req);

    void remove(JzdReportRemoveREQ req);

    void modify(JzdReportModifyREQ req);

    PageR<JzdReportListRSP> pageList(JzdReportListREQ req);
}
