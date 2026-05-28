package cn.zswltech.mithras.service.service.datacompare.factory;

import cn.zswltech.mithras.dto.client.relatedenterprise.CorpRelatedEnterpriseListRSP;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import cn.zswltech.mithras.service.mapper.lib.client.CorpRelatedEnterpriseLibMapper;
import cn.zswltech.mithras.service.mapper.model.client.CorpRelatedEnterprise;
import cn.zswltech.mithras.service.mapper.model.client.CorpRelatedEnterpriseLib;
import cn.zswltech.mithras.service.service.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.service.service.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.service.service.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.service.service.lib.client.handler.impl.CorpRelatedEnterpriseLibHandlerImpl;
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
        return new DefaultDataCompare<CorpRelatedEnterprise, CorpRelatedEnterpriseLib, CorpRelatedEnterpriseListRSP>(rsps, libMapper, handler, commonVersionMapper,BusinessModuleEnum.CLIENT.name(), version);
    }
}