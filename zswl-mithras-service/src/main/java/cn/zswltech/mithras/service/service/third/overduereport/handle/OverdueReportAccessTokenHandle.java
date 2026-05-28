package cn.zswltech.mithras.service.service.third.overduereport.handle;

import cn.zswltech.mithras.service.enums.third.FinancialDevUrlENUM;
import cn.zswltech.mithras.service.enums.third.FinancialUrlENUM;
import cn.zswltech.mithras.service.repository.PlatformApiEnum;
import cn.zswltech.mithras.service.service.third.overduereport.OverdueReportApiHandler;
import cn.zswltech.mithras.service.service.third.overduereport.OverdueReportConfigService;
import cn.zswltech.mithras.service.service.third.overduereport.req.OverdueReportAccessTokenREQ;
import cn.zswltech.mithras.service.service.third.overduereport.rsp.OverdueReportAccessTokenRSP;
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
public class OverdueReportAccessTokenHandle extends OverdueReportApiHandler<OverdueReportAccessTokenREQ, OverdueReportAccessTokenRSP> {

    @Resource
    private OverdueReportConfigService overdueReportConfigService;

    @Override
    public PlatformApiEnum platformApi() {
        return PlatformApiEnum.OVERDUE_REPORT_ACCESS_TOKEN;
    }

    @Override
    public String getUrl() {
        //测试环境
        if (!PROD.equals(active)) {
            return overdueReportConfigService.getUrl(FinancialDevUrlENUM.ACCESS_TOKEN_INFO.url);
        }
        return overdueReportConfigService.getUrl(FinancialUrlENUM.ACCESS_TOKEN_INFO.url);
    }

    @Override
    public OverdueReportAccessTokenRSP analyResponseResult(String response) {
        return JSONObject.parseObject(response, OverdueReportAccessTokenRSP.class);
    }

    @Override
    public OverdueReportAccessTokenRSP execute(OverdueReportAccessTokenREQ reqData) {
        return super.execute(reqData);
    }

    @Override
    public Map<String, String> getHttpHeadParam() {
        return new HashMap<>();
    }
}
