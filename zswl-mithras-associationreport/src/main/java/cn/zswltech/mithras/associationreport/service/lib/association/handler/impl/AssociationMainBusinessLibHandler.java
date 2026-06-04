package cn.zswltech.mithras.associationreport.service.lib.association.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.associationreport.AssociationDetailMainBusinessRSP;
import cn.zswltech.mithras.associationreport.mapper.model.AssociationMainBusiness;
import cn.zswltech.mithras.associationreport.mapper.model.AssociationMainBusinessLib;
import cn.zswltech.mithras.associationreport.service.lib.association.handler.AssociationReportLibAbstractHandler;
import org.springframework.stereotype.Service;

@Service
public class AssociationMainBusinessLibHandler extends AssociationReportLibAbstractHandler<AssociationMainBusinessLib, AssociationMainBusiness, AssociationDetailMainBusinessRSP> {
    @Override
    protected AssociationMainBusinessLib entity2Lib(AssociationMainBusiness f) {
        return BeanUtil.copyProperties(f, AssociationMainBusinessLib.class);
    }

    @Override
    protected AssociationMainBusiness lib2Entity(AssociationMainBusinessLib t) {
        return BeanUtil.copyProperties(t, AssociationMainBusiness.class);
    }

    @Override
    protected AssociationDetailMainBusinessRSP lib2Rsp(AssociationMainBusinessLib f) {
        AssociationDetailMainBusinessRSP rsp = BeanUtil.copyProperties(f, AssociationDetailMainBusinessRSP.class);
        return rsp;
    }


    @Override
    public boolean needHandle(Long clientId) {
        return true;
    }
}
