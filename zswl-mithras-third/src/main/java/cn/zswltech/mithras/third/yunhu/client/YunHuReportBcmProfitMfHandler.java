package cn.zswltech.mithras.third.yunhu.client;

import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.foundation.thirdparty.PlatformApiEnum;
import cn.zswltech.mithras.third.jinkong.client.req.ReportBcmAssetMfReq;
import cn.zswltech.mithras.third.jinkong.client.res.ReportBcmProfitMfRes;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

/**
 * @author dingqi
 * @date 2023/8/8
 * @description
 */
@Component
public class YunHuReportBcmProfitMfHandler extends YunHuApiHandler<ReportBcmAssetMfReq, ReportBcmProfitMfRes> {
    @Override
    protected String apiPath() {
        return "/easy-data-api/cncico_zsjk/caiwu/zszl_cwgl_cwbb_lrb_merge";
    }

    @Override
    protected HttpMethod httpMethod() {
        return HttpMethod.POST;
    }

    @Override
    public PlatformApiEnum platformApi() {
        return PlatformApiEnum.YUNHU_REPORT_BCM_JINKONG_PROFIT_MF;
    }

    @Override
    public ReportBcmProfitMfRes response(String data) {
        return JSONUtil.toBean(data, ReportBcmProfitMfRes.class);
    }
}
