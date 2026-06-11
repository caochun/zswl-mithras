package cn.zswltech.mithras.customer.datacompare;

import cn.zswltech.mithras.dto.client.relatedenterprise.CorpRelatedEnterpriseListRSP;
import cn.zswltech.mithras.foundation.persistence.mapper.CommonVersionMapper;
import cn.zswltech.mithras.customer.mapper.lib.client.CorpRelatedEnterpriseLibMapper;
import cn.zswltech.mithras.customer.mapper.model.client.CorpRelatedEnterprise;
import cn.zswltech.mithras.customer.mapper.model.client.CorpRelatedEnterpriseLib;
import cn.zswltech.mithras.foundation.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.foundation.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.foundation.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.customer.application.lib.client.handler.impl.CorpRelatedEnterpriseLibHandlerImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @create: 2022-08-04
 **/
@Service("corpRelatedEnterprise")
public class CorpRelatedEnterpriseFactory implements EditdataCompareFactory {

    @Resource
    private CorpRelatedEnterpriseLibMapper libMapper;
    @Resource
    private CorpRelatedEnterpriseLibHandlerImpl handler;
    @Resource
    private CommonVersionMapper commonVersionMapper;

    @Override
    public AbstractDataCompare createCompare(List rsps, String version) {
        return new DefaultDataCompare<CorpRelatedEnterprise, CorpRelatedEnterpriseLib, CorpRelatedEnterpriseListRSP>(rsps, libMapper, handler, commonVersionMapper,"CLIENT", version);
    }
}