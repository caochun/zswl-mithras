package cn.zswltech.mithras.third.service.jk;

import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.service.repository.PlatformApiEnum;
import cn.zswltech.mithras.third.service.jk.req.ReportBcmAssetMfReq;
import cn.zswltech.mithras.third.service.jk.res.ReportBcmCashflowMfRes;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

/**
 * @author dingqi
 * @date 2023/8/8
 * @description
 */
@Component
public class ReportBcmCashflowMfHandler extends JinKongApiHandler<ReportBcmAssetMfReq, ReportBcmCashflowMfRes> {
    @Override
    protected String apiPath() {
        return "/dm/dm/report_bcm/report_bcm_jinkong_cashflow_mf";
    }

    @Override
    protected HttpMethod httpMethod() {
        return HttpMethod.POST;
    }

    @Override
    public PlatformApiEnum platformApi() {
        return PlatformApiEnum.JK_REPORT_BCM_JINKONG_CASHFLOW_MF;
    }

    @Override
    public ReportBcmCashflowMfRes response(String data) {
        return JSONUtil.toBean(data, ReportBcmCashflowMfRes.class);
    }
}
