package cn.zswltech.mithras.riskcontrol.report.gljy;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.riskcontrol.model.gljy.report.GljyReportAddREQ;
import cn.zswltech.mithras.api.riskcontrol.model.gljy.report.GljyReportListREQ;
import cn.zswltech.mithras.api.riskcontrol.model.gljy.report.GljyReportListRSP;
import cn.zswltech.mithras.api.riskcontrol.model.gljy.report.GljyReportModifyREQ;
import cn.zswltech.mithras.api.riskcontrol.model.gljy.report.GljyReportRelatedClientREQ;
import cn.zswltech.mithras.api.riskcontrol.model.gljy.report.GljyReportRemoveREQ;
import cn.zswltech.mithras.api.riskcontrol.model.gljy.report.GljyReportSubmitREQ;

import java.util.List;

public interface RiskControlGljyReportApplicationService {

    void addManually(GljyReportAddREQ req);

    void remove(GljyReportRemoveREQ req);

    void modify(GljyReportModifyREQ req);

    PageR<GljyReportListRSP> pageList(GljyReportListREQ req);

    void submit(GljyReportSubmitREQ req);

    List<String> relatedClientList(GljyReportRelatedClientREQ req);
}
