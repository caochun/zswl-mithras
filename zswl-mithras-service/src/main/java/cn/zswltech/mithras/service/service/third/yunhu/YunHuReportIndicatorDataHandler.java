package cn.zswltech.mithras.service.service.third.yunhu;

import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.service.repository.PlatformApiEnum;
import cn.zswltech.mithras.service.service.third.yunhu.req.ReportIndicatorDataReq;
import cn.zswltech.mithras.service.service.third.yunhu.res.ReportIndicatorDataRes;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

/**
 * @author dingqi
 * @date 2025/10/9
 * @description 国资快报
 */
@Component
public class YunHuReportIndicatorDataHandler extends YunHuApiHandler<ReportIndicatorDataReq, ReportIndicatorDataRes> {
    @Override
    protected String apiPath() {
        return "/easy-data-api/cncico_zsjk/caiwu/zszl_cwgl_cwbb_gzkb";
    }

    @Override
    protected HttpMethod httpMethod() {
        return HttpMethod.POST;
    }

    @Override
    public PlatformApiEnum platformApi() {
        return PlatformApiEnum.YUNHU_REPORT_INDICATOR_DATA;
    }

    @Override
    public ReportIndicatorDataRes response(String data) {
        return JSONUtil.toBean(data, ReportIndicatorDataRes.class);
    }
}
