package cn.zswltech.mithras.riskcontrol.controller;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.riskcontrol.RiskControlGljyReportApi;
import cn.zswltech.mithras.api.riskcontrol.model.gljy.report.*;
import cn.zswltech.mithras.riskcontrol.report.gljy.RiskControlGljyReportApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author yibin
 */
@RestController
@Slf4j
public class RiskControlGljyReportController implements RiskControlGljyReportApi {

    @Resource
    private RiskControlGljyReportApplicationService gljyReportService;

    @Override
    public R<Void> addManually(GljyReportAddREQ req) {
        gljyReportService.addManually(req);
        return R.ok();
    }

    @Override
    public R<Void> remove(GljyReportRemoveREQ req) {
        gljyReportService.remove(req);
        return R.ok();
    }

    @Override
    public R<Void> modify(GljyReportModifyREQ req) {
        gljyReportService.modify(req);
        return R.ok();
    }

    @Override
    public R<PageR<GljyReportListRSP>> list(GljyReportListREQ req) {
        return R.ok(gljyReportService.pageList(req));
    }

    @Override
    public R<Void> submit(GljyReportSubmitREQ req) {
        gljyReportService.submit(req);
        return R.ok();
    }

    @Override
    public R<List<String>> relatedClientList(GljyReportRelatedClientREQ req) {
        return R.ok(gljyReportService.relatedClientList(req));
    }
}
