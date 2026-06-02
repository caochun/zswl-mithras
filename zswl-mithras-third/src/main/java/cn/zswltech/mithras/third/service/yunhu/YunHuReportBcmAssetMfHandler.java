package cn.zswltech.mithras.third.service.yunhu;

import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.service.repository.PlatformApiEnum;
import cn.zswltech.mithras.third.service.jk.JinKongApiHandler;
import cn.zswltech.mithras.third.service.jk.req.ReportBcmAssetMfReq;
import cn.zswltech.mithras.third.service.jk.res.ReportBcmAssetMfRes;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

/**
 * @author dingqi
 * @date 2023/8/8
 * @description
 */
@Component
public class YunHuReportBcmAssetMfHandler extends YunHuApiHandler<ReportBcmAssetMfReq, ReportBcmAssetMfRes> {
    @Override
    protected String apiPath() {
        return "/easy-data-api/cncico_zsjk/caiwu/zszl_cwgl_cwbb_zcfzb_merge";
    }

    @Override
    protected HttpMethod httpMethod() {
        return HttpMethod.POST;
    }

    @Override
    public PlatformApiEnum platformApi() {
        return PlatformApiEnum.YUNHU_REPORT_BCM_JINKONG_ASSET_MF;
    }

    @Override
    public ReportBcmAssetMfRes response(String data) {
        return JSONUtil.toBean(data, ReportBcmAssetMfRes.class);
    }
}
