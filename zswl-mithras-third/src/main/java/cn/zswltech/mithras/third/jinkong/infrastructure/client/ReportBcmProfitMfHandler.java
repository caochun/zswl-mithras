package cn.zswltech.mithras.third.jinkong.infrastructure.client;

import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.service.repository.PlatformApiEnum;
import cn.zswltech.mithras.third.jinkong.infrastructure.client.req.ReportBcmAssetMfReq;
import cn.zswltech.mithras.third.jinkong.infrastructure.client.res.ReportBcmProfitMfRes;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

/**
 * @author dingqi
 * @date 2023/8/8
 * @description
 */
@Component
public class ReportBcmProfitMfHandler extends JinKongApiHandler<ReportBcmAssetMfReq, ReportBcmProfitMfRes> {
    @Override
    protected String apiPath() {
        return "/dm/dm/report_bcm/report_bcm_jinkong_profit_mf";
    }

    @Override
    protected HttpMethod httpMethod() {
        return HttpMethod.POST;
    }

    @Override
    public PlatformApiEnum platformApi() {
        return PlatformApiEnum.JK_REPORT_BCM_JINKONG_PROFIT_MF;
    }

    @Override
    public ReportBcmProfitMfRes response(String data) {
        return JSONUtil.toBean(data, ReportBcmProfitMfRes.class);
    }
}
