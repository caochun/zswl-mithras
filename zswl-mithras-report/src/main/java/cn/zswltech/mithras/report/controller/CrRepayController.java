package cn.zswltech.mithras.report.controller;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.report.CrRepayApi;
import cn.zswltech.mithras.dto.report.repay.RepayListREQ;
import cn.zswltech.mithras.dto.report.repay.RepayListRSP;
import cn.zswltech.mithras.dto.report.repay.RepayModifyREQ;
import cn.zswltech.mithras.dto.version.DiffValue;
import cn.zswltech.mithras.report.auth.ReportAuthCheck;
import cn.zswltech.mithras.report.service.agg.CrRepayAggService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 征信报送-还款表接口
 *
 * @author wangchuanhao
 * @date 2023/1/11 4:02 PM
 */
@RestController
public class CrRepayController implements CrRepayApi {

    @Resource
    private CrRepayAggService crRepayAggService;

    @Override
    @ReportAuthCheck(type = ReportAuthCheck.AuthType.VIEW)
    public R<PageR<Map<String, DiffValue>>> list(RepayListREQ req) {
        return R.ok(crRepayAggService.list(req));
    }

    @Override
    @ReportAuthCheck(type = ReportAuthCheck.AuthType.EDIT)
    public R<Void> modify(RepayModifyREQ req) {
        crRepayAggService.modify(req);
        return R.ok();
    }

}
