package cn.zswltech.mithras.service.service.lib.association.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.associationreport.AssociationDetailEntityEconomyServiceRSP;
import cn.zswltech.mithras.service.mapper.model.associationreport.AssociationEntityEconomyService;
import cn.zswltech.mithras.service.mapper.model.associationreport.AssociationEntityEconomyServiceLib;
import cn.zswltech.mithras.service.service.lib.association.handler.AssociationReportLibAbstractHandler;
import org.springframework.stereotype.Service;

@Service
public class AssociationEntityEconomyServiceLibHandler extends AssociationReportLibAbstractHandler<AssociationEntityEconomyServiceLib, AssociationEntityEconomyService, AssociationDetailEntityEconomyServiceRSP> {
    @Override
    protected AssociationEntityEconomyServiceLib entity2Lib(AssociationEntityEconomyService f) {
        return BeanUtil.copyProperties(f, AssociationEntityEconomyServiceLib.class);
    }

    @Override
    protected AssociationEntityEconomyService lib2Entity(AssociationEntityEconomyServiceLib t) {
        return BeanUtil.copyProperties(t, AssociationEntityEconomyService.class);
    }

    @Override
    protected AssociationDetailEntityEconomyServiceRSP lib2Rsp(AssociationEntityEconomyServiceLib f) {
        AssociationDetailEntityEconomyServiceRSP rsp = BeanUtil.copyProperties(f, AssociationDetailEntityEconomyServiceRSP.class);
        return rsp;
    }


    @Override
    public boolean needHandle(Long clientId) {
        return true;
    }
}
