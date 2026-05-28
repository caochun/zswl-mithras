package cn.zswltech.mithras.report.controller;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.report.CrMortgageApi;
import cn.zswltech.mithras.dto.report.mortgage.MortgageListREQ;
import cn.zswltech.mithras.dto.report.mortgage.MortgageListRSP;
import cn.zswltech.mithras.dto.version.DiffValue;
import cn.zswltech.mithras.report.auth.ReportAuthCheck;
import cn.zswltech.mithras.report.service.agg.CrMortgageAggService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 征信报送-抵押表接口
 *
 * @author wangchuanhao
 * @date 2023/1/11 4:00 PM
 */
@RestController
public class CrMortgageController implements CrMortgageApi {

    @Resource
    private CrMortgageAggService crMortgageAggService;

    @Override
    @ReportAuthCheck(type = ReportAuthCheck.AuthType.VIEW)
    public R<PageR<Map<String, DiffValue>>> list(MortgageListREQ req) {
        return R.ok(crMortgageAggService.list(req));
    }

}
