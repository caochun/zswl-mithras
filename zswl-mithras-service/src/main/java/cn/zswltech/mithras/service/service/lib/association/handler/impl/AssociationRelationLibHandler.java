package cn.zswltech.mithras.service.service.lib.association.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.associationreport.AssociationDetailRelationRSP;
import cn.zswltech.mithras.service.mapper.model.associationreport.AssociationRelation;
import cn.zswltech.mithras.service.mapper.model.associationreport.AssociationRelationLib;
import cn.zswltech.mithras.service.service.lib.association.handler.AssociationReportLibAbstractHandler;
import org.springframework.stereotype.Service;

@Service
public class AssociationRelationLibHandler extends AssociationReportLibAbstractHandler<AssociationRelationLib, AssociationRelation, AssociationDetailRelationRSP> {
    @Override
    protected AssociationRelationLib entity2Lib(AssociationRelation f) {
        return BeanUtil.copyProperties(f, AssociationRelationLib.class);
    }

    @Override
    protected AssociationRelation lib2Entity(AssociationRelationLib t) {
        return BeanUtil.copyProperties(t, AssociationRelation.class);
    }

    @Override
    protected AssociationDetailRelationRSP lib2Rsp(AssociationRelationLib f) {
        AssociationDetailRelationRSP rsp = BeanUtil.copyProperties(f, AssociationDetailRelationRSP.class);
        return rsp;
    }


    @Override
    public boolean needHandle(Long clientId) {
        return true;
    }
}
