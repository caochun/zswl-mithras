package cn.zswltech.mithras.third.yunhu.client;

import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.foundation.thirdparty.PlatformApiEnum;
import cn.zswltech.mithras.third.jinkong.client.req.ReportBcmBalanceMfReq;
import cn.zswltech.mithras.third.jinkong.client.res.ReportBcmBalanceMfRes;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

/**
 * @author dingqi
 * @date 2023/8/8
 * @description
 */
@Component
public class YunHuReportBcmBalanceMfHandler extends YunHuApiHandler<ReportBcmBalanceMfReq, ReportBcmBalanceMfRes> {
    @Override
    protected String apiPath() {
        return "/easy-data-api/cncico_zsjk/caiwu/zszl_cwgl_cwbb_kmyeb";
    }

    @Override
    protected HttpMethod httpMethod() {
        return HttpMethod.POST;
    }

    @Override
    public PlatformApiEnum platformApi() {
        return PlatformApiEnum.YUNHU_REPORT_BCM_JINKONG_BALANCE_MF;
    }

    @Override
    public ReportBcmBalanceMfRes response(String data) {
        return JSONUtil.toBean(data, ReportBcmBalanceMfRes.class);
    }
}
