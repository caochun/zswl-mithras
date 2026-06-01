package cn.zswltech.mithras.service.service.third.overduereport.handle;

import cn.zswltech.mithras.service.enums.third.OverdueReportDevUrlENUM;
import cn.zswltech.mithras.service.enums.third.OverdueReportUrlENUM;
import cn.zswltech.mithras.service.repository.PlatformApiEnum;
import cn.zswltech.mithras.service.service.third.overduereport.OverdueReportApiHandler;
import cn.zswltech.mithras.service.service.third.overduereport.OverdueReportConfigService;
import cn.zswltech.mithras.service.service.third.overduereport.req.OverdueReportAppTokenREQ;
import cn.zswltech.mithras.service.service.third.overduereport.rsp.OverdueReportAppTokenRSP;
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
