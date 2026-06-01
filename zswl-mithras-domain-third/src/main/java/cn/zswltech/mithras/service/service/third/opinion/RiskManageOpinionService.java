package cn.zswltech.mithras.service.service.third.opinion;

import cn.zswltech.mithras.service.mapper.model.client.Client;
import cn.zswltech.mithras.service.repository.PlatformApiEnum;
import cn.zswltech.mithras.service.repository.PlatformApiHandleFactory;
import cn.zswltech.mithras.service.repository.PlatformApiHandler;
import cn.zswltech.mithras.service.service.third.opinion.req.RiskControlOpinionRegisterItem;
import cn.zswltech.mithras.service.service.third.opinion.resp.RiskControlCommRSP;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

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
     * @param clientList 客户列表
     **/
    public Boolean registerClient(List<Client> clientList) {
        PlatformApiHandler<List<RiskControlOpinionRegisterItem>, RiskControlCommRSP> platformApiHandler = platformApiHandleFactory.getPlatformApiHandler(PlatformApiEnum.PO_REGISTER);
        List<RiskControlOpinionRegisterItem> itemList = clientList.stream().map(e -> {
            RiskControlOpinionRegisterItem item = new RiskControlOpinionRegisterItem();
            item.setCompanyName(e.getClientName());
            item.setCreditCode(e.getUscCode());
            return item;
        }).collect(Collectors.toList());
        RiskControlCommRSP execute = platformApiHandler.execute(itemList);
        return platformApiHandler.isExecuteSuccess(execute);
    }

}
