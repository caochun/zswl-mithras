package cn.zswltech.mithras.report.controller;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.report.CrClientApi;
import cn.zswltech.mithras.dto.report.client.ClientListREQ;
import cn.zswltech.mithras.dto.report.client.ClientListRSP;
import cn.zswltech.mithras.dto.version.DiffValue;
import cn.zswltech.mithras.report.auth.ReportAuthCheck;
import cn.zswltech.mithras.report.service.agg.CrClientAggService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 征信报送-客户表接口
 *
 * @author wangchuanhao
 * @date 2023/1/11 3:55 PM
 */
@RestController
public class CrClientController implements CrClientApi {

    @Resource
    private CrClientAggService crClientAggService;

    @Override
    @ReportAuthCheck(type = ReportAuthCheck.AuthType.VIEW)
    public R<PageR<Map<String, DiffValue>>> list(ClientListREQ req) {
        return R.ok(crClientAggService.list(req));
    }

}
