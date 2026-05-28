package cn.zswltech.mithras.service.service.lib.association.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.associationreport.AssociationDetailExternalFinancingRSP;
import cn.zswltech.mithras.service.mapper.model.associationreport.AssociationExternalFinancing;
import cn.zswltech.mithras.service.mapper.model.associationreport.AssociationExternalFinancingLib;
import cn.zswltech.mithras.service.service.lib.association.handler.AssociationReportLibAbstractHandler;
import org.springframework.stereotype.Service;

@Service
public class AssociationExternalFinancingLibHandler extends AssociationReportLibAbstractHandler<AssociationExternalFinancingLib, AssociationExternalFinancing, AssociationDetailExternalFinancingRSP> {
    @Override
    protected AssociationExternalFinancingLib entity2Lib(AssociationExternalFinancing f) {
        return BeanUtil.copyProperties(f, AssociationExternalFinancingLib.class);
    }

    @Override
    protected AssociationExternalFinancing lib2Entity(AssociationExternalFinancingLib t) {
        return BeanUtil.copyProperties(t, AssociationExternalFinancing.class);
    }

    @Override
    protected AssociationDetailExternalFinancingRSP lib2Rsp(AssociationExternalFinancingLib f) {
        AssociationDetailExternalFinancingRSP rsp = BeanUtil.copyProperties(f, AssociationDetailExternalFinancingRSP.class);
        return rsp;
    }


    @Override
    public boolean needHandle(Long clientId) {
        return true;
    }
}
