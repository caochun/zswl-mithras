package cn.zswltech.mithras.report.controller;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.report.CrOverdueRecordApi;
import cn.zswltech.mithras.dto.report.overduerecord.OverdueRecordListREQ;
import cn.zswltech.mithras.dto.report.overduerecord.OverdueRecordListRSP;
import cn.zswltech.mithras.dto.report.overduerecord.OverdueRecordModifyREQ;
import cn.zswltech.mithras.dto.report.overduerecord.OverdueRecordRemoveREQ;
import cn.zswltech.mithras.dto.version.DiffValue;
import cn.zswltech.mithras.report.auth.ReportAuthCheck;
import cn.zswltech.mithras.report.service.agg.CrOverdueRecordAggService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 征信报送-逾期表接口
 *
 * @author wangchuanhao
 * @date 2023/1/11 4:00 PM
 */
@RestController
public class CrOverdueRecordController implements CrOverdueRecordApi {

    @Resource
    private CrOverdueRecordAggService crOverdueRecordAggService;

    @Override
    @ReportAuthCheck(type = ReportAuthCheck.AuthType.VIEW)
    public R<PageR<Map<String, DiffValue>>> list(OverdueRecordListREQ req) {
        return R.ok(crOverdueRecordAggService.list(req));
    }

    @Override
    @ReportAuthCheck(type = ReportAuthCheck.AuthType.EDIT)
    public R<Void> modify(OverdueRecordModifyREQ req) {
        crOverdueRecordAggService.modify(req);
        return R.ok();
    }

    @Override
    @ReportAuthCheck(type = ReportAuthCheck.AuthType.EDIT)
    public R<Void> remove(OverdueRecordRemoveREQ req) {
        crOverdueRecordAggService.remove(req);
        return R.ok();
    }

    @Override
    @ReportAuthCheck(type = ReportAuthCheck.AuthType.EDIT)
    public R<Void> cancelRemove(OverdueRecordRemoveREQ req) {
        crOverdueRecordAggService.cancelRemove(req);
        return R.ok();
    }

}
