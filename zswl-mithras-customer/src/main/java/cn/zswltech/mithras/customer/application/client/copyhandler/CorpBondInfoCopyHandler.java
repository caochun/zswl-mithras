package cn.zswltech.mithras.customer.application.client.copyhandler;

import cn.zswltech.mithras.customer.enums.InfoModule;
import cn.zswltech.mithras.customer.model.client.CorpBondInfo;
import cn.zswltech.mithras.customer.model.client.NewCorpBondInfo;
import cn.zswltech.mithras.customer.application.client.model.ClientCopyInfoBO;
import cn.zswltech.mithras.customer.application.client.CorpBondInfoService;
import cn.zswltech.mithras.customer.application.client.NewCorpBondInfoService;
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
public class CorpBondInfoCopyHandler extends AbstractClientDataCopyHandler<CorpBondInfo, NewCorpBondInfo, CorpBondInfoService, NewCorpBondInfoService> {
    @Resource
    private CorpBondInfoService oldCorpBondInfoService;
    @Resource
    private NewCorpBondInfoService newCorpBondInfoService;

    @Override
    protected CorpBondInfoService getOldService() {
        return oldCorpBondInfoService;
    }

    @Override
    protected NewCorpBondInfoService getNewService() {
        return newCorpBondInfoService;
    }

    @Override
    protected NewCorpBondInfo copyNewModel(CorpBondInfo corpBondInfo, ClientCopyInfoBO clientCopyInfoBO) {
        NewCorpBondInfo newCorpBondInfo = this.copyIgnoreBaseField(corpBondInfo, NewCorpBondInfo.class);
        newCorpBondInfo.setUserId(clientCopyInfoBO.getCurrentUserId());
        return newCorpBondInfo;
    }

    @Override
    protected CorpBondInfo copyOldModel(NewCorpBondInfo newCorpBondInfo, ClientCopyInfoBO clientCopyInfoBO) {
        return this.copyIgnoreBaseField(newCorpBondInfo, CorpBondInfo.class);
    }

    @Override
    protected NewCorpBondInfo copyNewModelFromNewModel(NewCorpBondInfo otherNewCorpBondInfo, ClientCopyInfoBO clientCopyInfoBO) {
        return this.copyNewModel(otherNewCorpBondInfo, clientCopyInfoBO);
    }

    @Override
    public InfoModule getInfoModule() {
        return InfoModule.CORP_BOND;
    }
}
