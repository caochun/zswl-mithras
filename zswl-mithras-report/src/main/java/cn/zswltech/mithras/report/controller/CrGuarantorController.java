package cn.zswltech.mithras.report.controller;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.report.CrGuarantorApi;
import cn.zswltech.mithras.dto.report.guarantor.GuarantorListREQ;
import cn.zswltech.mithras.dto.report.guarantor.GuarantorListRSP;
import cn.zswltech.mithras.dto.version.DiffValue;
import cn.zswltech.mithras.report.auth.ReportAuthCheck;
import cn.zswltech.mithras.report.service.agg.CrGuarantorAggService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 征信报送-保证表接口
 *
 * @author wangchuanhao
 * @date 2023/1/11 3:59 PM
 */
@RestController
public class CrGuarantorController implements CrGuarantorApi {

    @Resource
    private CrGuarantorAggService crGuarantorAggService;

    @Override
    @ReportAuthCheck(type = ReportAuthCheck.AuthType.VIEW)
    public R<PageR<Map<String, DiffValue>>> list(GuarantorListREQ req) {
        return R.ok(crGuarantorAggService.list(req));
    }

}
