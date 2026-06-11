package cn.zswltech.mithras.customer.application.client.copyhandler;

import cn.zswltech.mithras.customer.enums.InfoModule;
import cn.zswltech.mithras.customer.mapper.model.client.CorpShareholderInfo;
import cn.zswltech.mithras.customer.mapper.model.client.NewCorpShareholderInfo;
import cn.zswltech.mithras.customer.application.client.bo.ClientCopyInfoBO;
import cn.zswltech.mithras.customer.application.client.CorpShareHolderInfoService;
import cn.zswltech.mithras.customer.application.client.NewCorpShareHolderInfoService;
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
public class CorpShareholderInfoCopyHandler extends AbstractClientDataCopyHandler<CorpShareholderInfo, NewCorpShareholderInfo, CorpShareHolderInfoService, NewCorpShareHolderInfoService> {
    @Resource
    private CorpShareHolderInfoService oldCorpShareHolderInfoService;
    @Resource
    private NewCorpShareHolderInfoService newCorpShareHolderInfoService;

    @Override
    protected CorpShareHolderInfoService getOldService() {
        return oldCorpShareHolderInfoService;
    }

    @Override
    protected NewCorpShareHolderInfoService getNewService() {
        return newCorpShareHolderInfoService;
    }

    @Override
    protected NewCorpShareholderInfo copyNewModel(CorpShareholderInfo corpShareholderInfo, ClientCopyInfoBO clientCopyInfoBO) {
        NewCorpShareholderInfo newCorpShareholderInfo = this.copyIgnoreBaseField(corpShareholderInfo, NewCorpShareholderInfo.class);
        newCorpShareholderInfo.setUserId(clientCopyInfoBO.getCurrentUserId());
        return newCorpShareholderInfo;
    }

    @Override
    protected CorpShareholderInfo copyOldModel(NewCorpShareholderInfo newCorpShareholderInfo, ClientCopyInfoBO clientCopyInfoBO) {
        return this.copyIgnoreBaseField(newCorpShareholderInfo, CorpShareholderInfo.class);
    }

    @Override
    protected NewCorpShareholderInfo copyNewModelFromNewModel(NewCorpShareholderInfo otherNewCorpShareholderInfo, ClientCopyInfoBO clientCopyInfoBO) {
        return this.copyNewModel(otherNewCorpShareholderInfo, clientCopyInfoBO);
    }

    @Override
    public InfoModule getInfoModule() {
        return InfoModule.CORP_SHAREHOLDER;
    }
}
