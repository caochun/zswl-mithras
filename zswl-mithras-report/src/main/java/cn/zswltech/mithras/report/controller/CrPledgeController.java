package cn.zswltech.mithras.report.controller;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.report.CrPledgeApi;
import cn.zswltech.mithras.dto.report.pledge.PledgeListREQ;
import cn.zswltech.mithras.dto.version.DiffValue;
import cn.zswltech.mithras.report.auth.ReportAuthCheck;
import cn.zswltech.mithras.report.service.agg.CrPledgeAggService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 征信报送-质押表接口
 *
 * @author wangchuanhao
 * @date 2023/1/11 4:02 PM
 */
@RestController
public class CrPledgeController implements CrPledgeApi {

    @Resource
    private CrPledgeAggService crPledgeAggService;

    @Override
    @ReportAuthCheck(type = ReportAuthCheck.AuthType.VIEW)
    public R<PageR<Map<String, DiffValue>>> list(PledgeListREQ req) {
        return R.ok(crPledgeAggService.list(req));
    }

}
