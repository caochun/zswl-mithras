package cn.zswltech.mithras.service.service.third.yunhu;

import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.service.repository.PlatformApiEnum;
import cn.zswltech.mithras.service.service.third.jk.JinKongApiHandler;
import cn.zswltech.mithras.service.service.third.jk.req.ReportBcmFflexfiledAssistMfReq;
import cn.zswltech.mithras.service.service.third.jk.res.ReportBcmFflexfiledAssistMfRes;
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
