package cn.zswltech.mithras.riskcontrol.interfaces;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.riskcontrol.RiskControlJzdReportApi;
import cn.zswltech.mithras.api.riskcontrol.model.*;
import cn.zswltech.mithras.riskcontrol.report.jzd.RiskControlJzdReportApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author yibin
 */
@RestController
public class RiskControlJzdController implements RiskControlJzdReportApi {

    @Resource
    private RiskControlJzdReportApplicationService jzdReportService;

    @Override
    public R<Void> submit(JzdReportSubmitREQ req) {
        jzdReportService.submit(req);
        return R.ok();
    }

    @Override
    public R<Void> addManually(JzdReportAddREQ req) {
        jzdReportService.addManually(req);
        return R.ok();
    }

    @Override
    public R<Void> remove(JzdReportRemoveREQ req) {
        jzdReportService.remove(req);
        return R.ok();
    }

    @Override
    public R<Void> modify(JzdReportModifyREQ req) {
        jzdReportService.modify(req);
        return R.ok();
    }

    @Override
    public R<PageR<JzdReportListRSP>> list(JzdReportListREQ req) {
        return R.ok(jzdReportService.pageList(req));
    }
}
