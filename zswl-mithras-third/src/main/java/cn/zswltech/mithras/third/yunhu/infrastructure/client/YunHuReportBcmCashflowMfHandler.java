package cn.zswltech.mithras.third.yunhu.infrastructure.client;

import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.service.repository.PlatformApiEnum;
import cn.zswltech.mithras.third.jinkong.infrastructure.client.JinKongApiHandler;
import cn.zswltech.mithras.third.jinkong.infrastructure.client.req.ReportBcmAssetMfReq;
import cn.zswltech.mithras.third.jinkong.infrastructure.client.res.ReportBcmCashflowMfRes;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

/**
 * @author dingqi
 * @date 2023/8/8
 * @description
 */
@Component
public class YunHuReportBcmCashflowMfHandler extends YunHuApiHandler<ReportBcmAssetMfReq, ReportBcmCashflowMfRes> {
    @Override
    protected String apiPath() {
        return "/easy-data-api/cncico_zsjk/caiwu/zszl_cwgl_cwbb_xjllb_merge";
    }

    @Override
    protected HttpMethod httpMethod() {
        return HttpMethod.POST;
    }

    @Override
    public PlatformApiEnum platformApi() {
        return PlatformApiEnum.YUNHU_REPORT_BCM_JINKONG_CASHFLOW_MF;
    }

    @Override
    public ReportBcmCashflowMfRes response(String data) {
        return JSONUtil.toBean(data, ReportBcmCashflowMfRes.class);
    }
}
