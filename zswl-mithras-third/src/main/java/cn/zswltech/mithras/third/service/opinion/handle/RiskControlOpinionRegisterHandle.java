package cn.zswltech.mithras.third.service.opinion.handle;

import cn.zswltech.mithras.service.repository.PlatformApiEnum;
import cn.zswltech.mithras.third.service.opinion.RiskManageApiHandler;
import cn.zswltech.mithras.third.service.opinion.req.RiskControlOpinionRegisterItem;
import cn.zswltech.mithras.third.service.opinion.resp.RiskControlCommRSP;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName RiskControlOpinionRegisterHandle
 * @Description TODO
 * @Author jackerhe
 * @Date 2023/3/10 10:50 上午
 * @Version 1.0
 **/
@Component
public class RiskControlOpinionRegisterHandle extends RiskManageApiHandler<List<RiskControlOpinionRegisterItem>, RiskControlCommRSP> {

    private final static String REGISTER_URL = "/gungnirApi/opinion/customer/zlRegisterList";

    @Value("${risk.control.baseUrl:}")
    private String riskControl;

    @Override
    public PlatformApiEnum platformApi() {
        return PlatformApiEnum.PO_REGISTER;
    }

    @Override
    public String getUrl() {
        return riskControl + REGISTER_URL;
    }

    @Override
    public Map<String, String> getHttpHeadParam() {
        return new HashMap<>();
    }

    @Override
    public RiskControlCommRSP analyResponseResult(String response) {
        return JSONObject.parseObject(response, RiskControlCommRSP.class);
    }
}
