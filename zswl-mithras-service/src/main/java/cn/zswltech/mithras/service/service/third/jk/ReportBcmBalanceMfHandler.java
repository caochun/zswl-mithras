package cn.zswltech.mithras.service.service.third.jk;

import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.service.repository.PlatformApiEnum;
import cn.zswltech.mithras.service.service.third.jk.req.ReportBcmBalanceMfReq;
import cn.zswltech.mithras.service.service.third.jk.res.ReportBcmBalanceMfRes;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

/**
 * @author dingqi
 * @date 2023/8/8
 * @description
 */
@Component
public class ReportBcmBalanceMfHandler extends JinKongApiHandler<ReportBcmBalanceMfReq, ReportBcmBalanceMfRes> {
    @Override
    protected String apiPath() {
        return "/dm/dm/report_bcm/report_bcm_jinkong_balance_mf";
    }

    @Override
    protected HttpMethod httpMethod() {
        return HttpMethod.POST;
    }

    @Override
    public PlatformApiEnum platformApi() {
        return PlatformApiEnum.JK_REPORT_BCM_JINKONG_BALANCE_MF;
    }

    @Override
    public ReportBcmBalanceMfRes response(String data) {
        return JSONUtil.toBean(data, ReportBcmBalanceMfRes.class);
    }
}
