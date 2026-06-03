package cn.zswltech.mithras.third.overduereport.infrastructure.client.handle;

import cn.zswltech.mithras.third.enums.OverdueReportDevUrlENUM;
import cn.zswltech.mithras.third.enums.OverdueReportUrlENUM;
import cn.zswltech.mithras.service.repository.PlatformApiEnum;
import cn.zswltech.mithras.third.overduereport.infrastructure.client.OverdueReportApiHandler;
import cn.zswltech.mithras.third.overduereport.application.OverdueReportConfigService;
import cn.zswltech.mithras.third.overduereport.infrastructure.client.req.OverdueReportAppTokenREQ;
import cn.zswltech.mithras.third.overduereport.infrastructure.client.rsp.OverdueReportAppTokenRSP;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName AppTokenHandle
 * @Description
 * @Author jackerhe
 * @Date 2022/10/27 11:10 上午
 * @Version 1.0
 **/
@Component
public class OverdueReportAppTokenHandle extends OverdueReportApiHandler<OverdueReportAppTokenREQ, OverdueReportAppTokenRSP> {

    @Resource
    private OverdueReportConfigService overdueReportConfigService;

    @Override
    public PlatformApiEnum platformApi() {
        return PlatformApiEnum.OVERDUE_REPORT_APP_TOKEN;
    }

    @Override
    public String getUrl() {
        //测试环境
        if (!PROD.equals(active)) {
            return overdueReportConfigService.getUrl(OverdueReportDevUrlENUM.OVERDUE_REPORT_APP_TOKEN.url);
        }
        return overdueReportConfigService.getUrl(OverdueReportUrlENUM.OVERDUE_REPORT_APP_TOKEN.url);
    }

    @Override
    public OverdueReportAppTokenRSP analyResponseResult(String response) {
        return JSONObject.parseObject(response, OverdueReportAppTokenRSP.class);
    }

    @Override
    public OverdueReportAppTokenRSP execute(OverdueReportAppTokenREQ reqData) {
        return super.execute(reqData);
    }

    @Override
    public Map<String, String> getHttpHeadParam() {
       return new HashMap<>();
    }
}
