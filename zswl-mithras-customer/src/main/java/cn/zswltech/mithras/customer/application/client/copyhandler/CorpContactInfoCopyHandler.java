package cn.zswltech.mithras.customer.application.client.copyhandler;

import cn.zswltech.mithras.customer.enums.InfoModule;
import cn.zswltech.mithras.customer.model.client.CorpContactInfo;
import cn.zswltech.mithras.customer.model.client.NewCorpContactInfo;
import cn.zswltech.mithras.customer.application.client.model.ClientCopyInfoBO;
import cn.zswltech.mithras.customer.application.client.CorpContactInfoService;
import cn.zswltech.mithras.customer.application.client.NewCorpContactInfoService;
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
public class CorpContactInfoCopyHandler extends AbstractClientDataCopyHandler<CorpContactInfo, NewCorpContactInfo, CorpContactInfoService, NewCorpContactInfoService> {
    @Resource
    private CorpContactInfoService oldCorpContactInfoService;
    @Resource
    private NewCorpContactInfoService newCorpContactInfoService;

    @Override
    protected CorpContactInfoService getOldService() {
        return oldCorpContactInfoService;
    }

    @Override
    protected NewCorpContactInfoService getNewService() {
        return newCorpContactInfoService;
    }

    @Override
    protected NewCorpContactInfo copyNewModel(CorpContactInfo corpContactInfo, ClientCopyInfoBO clientCopyInfoBO) {
        NewCorpContactInfo newCorpContactInfo = this.copyIgnoreBaseField(corpContactInfo, NewCorpContactInfo.class);
        newCorpContactInfo.setUserId(clientCopyInfoBO.getCurrentUserId());
        return newCorpContactInfo;
    }

    @Override
    protected CorpContactInfo copyOldModel(NewCorpContactInfo newCorpContactInfo, ClientCopyInfoBO clientCopyInfoBO) {
        return this.copyIgnoreBaseField(newCorpContactInfo, CorpContactInfo.class);
    }

    @Override
    protected NewCorpContactInfo copyNewModelFromNewModel(NewCorpContactInfo otherNewCorpContactInfo, ClientCopyInfoBO clientCopyInfoBO) {
        return this.copyNewModel(otherNewCorpContactInfo, clientCopyInfoBO);
    }

    @Override
    public InfoModule getInfoModule() {
        return InfoModule.CORP_CONTACT;
    }
}
