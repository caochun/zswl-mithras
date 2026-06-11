package cn.zswltech.mithras.third.financialshare.client.handle;

import cn.zswltech.mithras.third.financialshare.enums.FinancialDevUrlENUM;
import cn.zswltech.mithras.third.financialshare.enums.FinancialUrlENUM;
import cn.zswltech.mithras.foundation.thirdparty.PlatformApiEnum;
import cn.zswltech.mithras.third.financialshare.client.FinancialApiHandler;
import cn.zswltech.mithras.third.financialshare.application.FinancialConfigService;
import cn.zswltech.mithras.third.financialshare.client.req.AppTokenREQ;
import cn.zswltech.mithras.third.financialshare.client.resp.AppTokenRSP;
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
public class AppTokenHandle extends FinancialApiHandler<AppTokenREQ, AppTokenRSP> {

    @Resource
    private FinancialConfigService financialConfigService;

    @Override
    public PlatformApiEnum platformApi() {
        return PlatformApiEnum.CQ_APP_TOKEN;
    }

    @Override
    public String getUrl() {
        //测试环境
        if (!PROD.equals(active)) {
            return financialConfigService.getUrl(FinancialDevUrlENUM.APP_TOKEN_INFO.url);
        }
        return financialConfigService.getUrl(FinancialUrlENUM.APP_TOKEN_INFO.url);
    }

    @Override
    public AppTokenRSP analyResponseResult(String response) {
        return JSONObject.parseObject(response, AppTokenRSP.class);
    }

    @Override
    public AppTokenRSP execute(AppTokenREQ reqData) {
        return super.execute(reqData);
    }

    @Override
    public Map<String, String> getHttpHeadParam() {
       return new HashMap<>();
    }
}
