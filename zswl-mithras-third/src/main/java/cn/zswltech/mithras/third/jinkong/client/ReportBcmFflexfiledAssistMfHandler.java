package cn.zswltech.mithras.third.jinkong.client;

import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.foundation.thirdparty.PlatformApiEnum;
import cn.zswltech.mithras.third.jinkong.client.req.ReportBcmFflexfiledAssistMfReq;
import cn.zswltech.mithras.third.jinkong.client.res.ReportBcmFflexfiledAssistMfRes;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

/**
 * @author dingqi
 * @date 2023/8/9
 * @description
 */
@Component
public class ReportBcmFflexfiledAssistMfHandler extends JinKongApiHandler<ReportBcmFflexfiledAssistMfReq, ReportBcmFflexfiledAssistMfRes> {
    @Override
    protected String apiPath() {
        return "/dm/dm/report_bcm/report_bcm_jinkong_fflexfiled_assist_mf";
    }

    @Override
    protected HttpMethod httpMethod() {
        return HttpMethod.POST;
    }

    @Override
    public PlatformApiEnum platformApi() {
        return PlatformApiEnum.JK_REPORT_BCM_JINKONG_FFLEXFILED_ASSIST_MF;
    }

    @Override
    public ReportBcmFflexfiledAssistMfRes response(String data) {
        return JSONUtil.toBean(data, ReportBcmFflexfiledAssistMfRes.class);
    }
}
