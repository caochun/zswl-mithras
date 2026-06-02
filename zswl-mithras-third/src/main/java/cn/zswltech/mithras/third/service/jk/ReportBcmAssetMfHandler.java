package cn.zswltech.mithras.third.service.jk;

import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.service.repository.PlatformApiEnum;
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
public class ReportBcmAssetMfHandler extends JinKongApiHandler<ReportBcmAssetMfReq, ReportBcmAssetMfRes> {
    @Override
    protected String apiPath() {
        return "/dm/dm/report_bcm/report_bcm_jinkong_asset_mf";
    }

    @Override
    protected HttpMethod httpMethod() {
        return HttpMethod.POST;
    }

    @Override
    public PlatformApiEnum platformApi() {
        return PlatformApiEnum.JK_REPORT_BCM_JINKONG_ASSET_MF;
    }

    @Override
    public ReportBcmAssetMfRes response(String data) {
        return JSONUtil.toBean(data, ReportBcmAssetMfRes.class);
    }
}
