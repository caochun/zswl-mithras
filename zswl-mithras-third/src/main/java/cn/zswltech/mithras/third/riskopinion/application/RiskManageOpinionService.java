package cn.zswltech.mithras.third.riskopinion.application;

import cn.zswltech.mithras.foundation.thirdparty.PlatformApiEnum;
import cn.zswltech.mithras.foundation.thirdparty.PlatformApiHandleFactory;
import cn.zswltech.mithras.foundation.thirdparty.PlatformApiHandler;
import cn.zswltech.mithras.third.riskopinion.client.req.RiskControlOpinionRegisterItem;
import cn.zswltech.mithras.third.riskopinion.client.resp.RiskControlCommRSP;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName RiskManageOpinionService
 * @Description TODO
 * @Author jackerhe
 * @Date 2023/3/14 5:09 下午
 * @Version 1.0
 **/
@Component
public class RiskManageOpinionService {

    @Resource
    private PlatformApiHandleFactory platformApiHandleFactory;

    /**
     * @param itemList 客户注册项
     **/
    public Boolean registerClient(List<RiskControlOpinionRegisterItem> itemList) {
        PlatformApiHandler<List<RiskControlOpinionRegisterItem>, RiskControlCommRSP> platformApiHandler = platformApiHandleFactory.getPlatformApiHandler(PlatformApiEnum.PO_REGISTER);
        RiskControlCommRSP execute = platformApiHandler.execute(itemList);
        return platformApiHandler.isExecuteSuccess(execute);
    }

}
