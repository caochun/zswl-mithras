package cn.zswltech.mithras.customer.application.client.copyhandler;

import cn.zswltech.mithras.customer.domain.enums.InfoModule;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.CorpCommerceInfo;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.NewCorpCommerceInfo;
import cn.zswltech.mithras.customer.application.bo.ClientCopyInfoBO;
import cn.zswltech.mithras.customer.application.client.CorpCommerceInfoService;
import cn.zswltech.mithras.customer.application.client.NewCorpCommerceInfoService;
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
public class CorpCommerceInfoCopyHandler extends AbstractClientDataCopyHandler<CorpCommerceInfo, NewCorpCommerceInfo, CorpCommerceInfoService, NewCorpCommerceInfoService> {
    @Resource
    private CorpCommerceInfoService oldCorpCommerceInfoService;
    @Resource
    private NewCorpCommerceInfoService newCorpCommerceInfoService;

    @Override
    protected CorpCommerceInfoService getOldService() {
        return oldCorpCommerceInfoService;
    }

    @Override
    protected NewCorpCommerceInfoService getNewService() {
        return newCorpCommerceInfoService;
    }

    @Override
    protected NewCorpCommerceInfo copyNewModel(CorpCommerceInfo corpCommerceInfo, ClientCopyInfoBO clientCopyInfoBO) {
        NewCorpCommerceInfo newCorpCommerceInfo = this.copyIgnoreBaseField(corpCommerceInfo, NewCorpCommerceInfo.class);
        newCorpCommerceInfo.setUserId(clientCopyInfoBO.getCurrentUserId());
        return newCorpCommerceInfo;
    }

    @Override
    protected CorpCommerceInfo copyOldModel(NewCorpCommerceInfo newCorpCommerceInfo, ClientCopyInfoBO clientCopyInfoBO) {
        return this.copyIgnoreBaseField(newCorpCommerceInfo, CorpCommerceInfo.class);
    }

    @Override
    protected NewCorpCommerceInfo copyNewModelFromNewModel(NewCorpCommerceInfo otherNewCorpCommerceInfo, ClientCopyInfoBO clientCopyInfoBO) {
        return this.copyNewModel(otherNewCorpCommerceInfo, clientCopyInfoBO);
    }

    @Override
    public InfoModule getInfoModule() {
        return InfoModule.CORP_COMMERCE;
    }
}
