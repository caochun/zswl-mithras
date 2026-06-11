package cn.zswltech.mithras.customer.application.client.copyhandler;

import cn.zswltech.mithras.customer.enums.InfoModule;
import cn.zswltech.mithras.customer.model.client.CorpAddressInfo;
import cn.zswltech.mithras.customer.model.client.NewCorpAddressInfo;
import cn.zswltech.mithras.customer.application.client.bo.ClientCopyInfoBO;
import cn.zswltech.mithras.customer.application.client.CorpAddressInfoService;
import cn.zswltech.mithras.customer.application.client.NewCorpAddressInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author dingqi
 * @date 2024/9/11
 * @description
 */
@Slf4j
@Component
public class CorpAddressInfoCopyHandler extends AbstractClientDataCopyHandler<CorpAddressInfo, NewCorpAddressInfo, CorpAddressInfoService, NewCorpAddressInfoService> {
    @Resource
    private CorpAddressInfoService oldCorpAddressInfoService;
    @Resource
    private NewCorpAddressInfoService newCorpAddressInfoService;

    @Override
    protected CorpAddressInfoService getOldService() {
        return oldCorpAddressInfoService;
    }

    @Override
    protected NewCorpAddressInfoService getNewService() {
        return newCorpAddressInfoService;
    }

    @Override
    protected NewCorpAddressInfo copyNewModel(CorpAddressInfo corpAddressInfo, ClientCopyInfoBO clientCopyInfoBO) {
        NewCorpAddressInfo newCorpAddressInfo = this.copyIgnoreBaseField(corpAddressInfo, NewCorpAddressInfo.class);
        newCorpAddressInfo.setUserId(clientCopyInfoBO.getCurrentUserId());
        return newCorpAddressInfo;
    }

    @Override
    protected CorpAddressInfo copyOldModel(NewCorpAddressInfo newCorpAddressInfo, ClientCopyInfoBO clientCopyInfoBO) {
        return this.copyIgnoreBaseField(newCorpAddressInfo, CorpAddressInfo.class);
    }

    @Override
    protected NewCorpAddressInfo copyNewModelFromNewModel(NewCorpAddressInfo otherNewCorpAddressInfo, ClientCopyInfoBO clientCopyInfoBO) {
        return this.copyNewModel(otherNewCorpAddressInfo, clientCopyInfoBO);
    }

    @Override
    public InfoModule getInfoModule() {
        return InfoModule.CORP_ADDRESS;
    }
}
