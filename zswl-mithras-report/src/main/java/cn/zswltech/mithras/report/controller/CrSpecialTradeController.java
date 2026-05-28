package cn.zswltech.mithras.report.controller;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.report.CrSpecialTradeApi;
import cn.zswltech.mithras.dto.report.ReportChangeREQ;
import cn.zswltech.mithras.dto.report.specialtrade.SpecialTradeListREQ;
import cn.zswltech.mithras.dto.report.specialtrade.SpecialTradeListRSP;
import cn.zswltech.mithras.dto.report.specialtrade.SpecialTradeModifyREQ;
import cn.zswltech.mithras.dto.version.DiffValue;
import cn.zswltech.mithras.report.auth.ReportAuthCheck;
import cn.zswltech.mithras.report.service.agg.CrSpecialTradeAggService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 征信报送-特定交易表接口
 *
 * @author wangchuanhao
 * @date 2023/1/11 4:03 PM
 */
@RestController
public class CrSpecialTradeController implements CrSpecialTradeApi {

    @Resource
    private CrSpecialTradeAggService crSpecialTradeAggService;

    @Override
    @ReportAuthCheck(type = ReportAuthCheck.AuthType.VIEW)
    public R<PageR<Map<String, DiffValue>>> list(SpecialTradeListREQ req) {
        return R.ok(crSpecialTradeAggService.list(req));
    }

    @Override
    @ReportAuthCheck(type = ReportAuthCheck.AuthType.EDIT)
    public R<Void> modify(SpecialTradeModifyREQ req) {
        crSpecialTradeAggService.modify(req);
        return R.ok();
    }

    @Override
    @ReportAuthCheck(type = ReportAuthCheck.AuthType.EDIT)
    public R<Void> reportChange(ReportChangeREQ req) {
        crSpecialTradeAggService.reportChange(req);
        return R.ok();
    }

}
