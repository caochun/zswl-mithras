package cn.zswltech.mithras.service.service.third.yunhu;

import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.service.repository.PlatformApiEnum;
import cn.zswltech.mithras.service.service.third.jk.JinKongApiHandler;
import cn.zswltech.mithras.service.service.third.jk.req.ReportBcmAssetMfReq;
import cn.zswltech.mithras.service.service.third.jk.res.ReportBcmCashflowMfRes;
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
