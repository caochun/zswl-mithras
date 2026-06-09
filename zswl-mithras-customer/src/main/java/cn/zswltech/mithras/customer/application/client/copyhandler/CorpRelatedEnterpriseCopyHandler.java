package cn.zswltech.mithras.customer.application.client.copyhandler;

import cn.zswltech.mithras.customer.domain.enums.InfoModule;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.CorpRelatedEnterprise;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.NewCorpRelatedEnterprise;
import cn.zswltech.mithras.customer.application.bo.ClientCopyInfoBO;
import cn.zswltech.mithras.customer.application.client.CorpRelatedEnterpriseService;
import cn.zswltech.mithras.customer.application.client.NewCorpRelatedEnterpriseService;
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
public class CorpRelatedEnterpriseCopyHandler extends AbstractClientDataCopyHandler<CorpRelatedEnterprise, NewCorpRelatedEnterprise, CorpRelatedEnterpriseService, NewCorpRelatedEnterpriseService> {
    @Resource
    private CorpRelatedEnterpriseService oldCorpRelatedEnterpriseService;
    @Resource
    private NewCorpRelatedEnterpriseService newCorpRelatedEnterpriseService;

    @Override
    protected CorpRelatedEnterpriseService getOldService() {
        return oldCorpRelatedEnterpriseService;
    }

    @Override
    protected NewCorpRelatedEnterpriseService getNewService() {
        return newCorpRelatedEnterpriseService;
    }

    @Override
    protected NewCorpRelatedEnterprise copyNewModel(CorpRelatedEnterprise corpRelatedEnterprise, ClientCopyInfoBO clientCopyInfoBO) {
        NewCorpRelatedEnterprise newCorpRelatedEnterprise = this.copyIgnoreBaseField(corpRelatedEnterprise, NewCorpRelatedEnterprise.class);
        newCorpRelatedEnterprise.setUserId(clientCopyInfoBO.getCurrentUserId());
        return newCorpRelatedEnterprise;
    }

    @Override
    protected CorpRelatedEnterprise copyOldModel(NewCorpRelatedEnterprise newCorpRelatedEnterprise, ClientCopyInfoBO clientCopyInfoBO) {
        return this.copyIgnoreBaseField(newCorpRelatedEnterprise, CorpRelatedEnterprise.class);
    }

    @Override
    protected NewCorpRelatedEnterprise copyNewModelFromNewModel(NewCorpRelatedEnterprise otherNewCorpRelatedEnterprise, ClientCopyInfoBO clientCopyInfoBO) {
        return this.copyNewModel(otherNewCorpRelatedEnterprise, clientCopyInfoBO);
    }

    @Override
    public InfoModule getInfoModule() {
        return InfoModule.CORP_RELATED_ENTERPRISE;
    }
}
