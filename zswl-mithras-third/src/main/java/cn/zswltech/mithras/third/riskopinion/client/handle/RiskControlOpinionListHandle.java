package cn.zswltech.mithras.third.riskopinion.client.handle;

import cn.zswltech.mithras.foundation.thirdparty.PlatformApiEnum;
import cn.zswltech.mithras.third.riskopinion.client.RiskManageApiHandler;
import cn.zswltech.mithras.third.riskopinion.client.req.RiskControlOpinionPullListREQ;
import cn.zswltech.mithras.third.riskopinion.client.resp.RiskControlOpinionPullListRsp;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName RiskControlOpinionRegisterHandle
 * @Description TODO
 * @Author jackerhe
 * @Date 2023/3/10 10:50 上午
 * @Version 1.0
 **/
@Component
public class RiskControlOpinionListHandle extends RiskManageApiHandler<RiskControlOpinionPullListREQ, RiskControlOpinionPullListRsp> {

    private final static String LIST_URL = "/gungnirApi/opinion/info/zlIncList";

    @Value("${risk.control.baseUrl:}")
    private String riskControl;

    @Override
    public PlatformApiEnum platformApi() {
        return PlatformApiEnum.PO_LIST;
    }

    @Override
    public String getUrl() {
        return riskControl + LIST_URL;
    }

    @Override
    public Map<String, String> getHttpHeadParam() {
        return new HashMap<>();
    }

    @Override
    public RiskControlOpinionPullListRsp analyResponseResult(String response) {
        return JSONObject.parseObject(response, RiskControlOpinionPullListRsp.class);
    }
}
