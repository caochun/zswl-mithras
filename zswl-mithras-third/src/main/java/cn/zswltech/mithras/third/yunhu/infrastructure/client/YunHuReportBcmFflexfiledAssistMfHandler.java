package cn.zswltech.mithras.third.yunhu.infrastructure.client;

import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.service.repository.PlatformApiEnum;
import cn.zswltech.mithras.third.jinkong.infrastructure.client.JinKongApiHandler;
import cn.zswltech.mithras.third.jinkong.infrastructure.client.req.ReportBcmFflexfiledAssistMfReq;
import cn.zswltech.mithras.third.jinkong.infrastructure.client.res.ReportBcmFflexfiledAssistMfRes;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

/**
 * @author dingqi
 * @date 2023/8/9
 * @description
 */
@Component
public class YunHuReportBcmFflexfiledAssistMfHandler extends YunHuApiHandler<ReportBcmFflexfiledAssistMfReq, ReportBcmFflexfiledAssistMfRes> {
    @Override
    protected String apiPath() {
        return "/easy-data-api/cncico_zsjk/caiwu/zszl_cwgl_cwbb_fzhsb";
    }

    @Override
    protected HttpMethod httpMethod() {
        return HttpMethod.POST;
    }

    @Override
    public PlatformApiEnum platformApi() {
        return PlatformApiEnum.YUNHU_REPORT_BCM_JINKONG_FFLEXFILED_ASSIST_MF;
    }

    @Override
    public ReportBcmFflexfiledAssistMfRes response(String data) {
        return JSONUtil.toBean(data, ReportBcmFflexfiledAssistMfRes.class);
    }
}
