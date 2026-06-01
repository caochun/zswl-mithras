package cn.zswltech.mithras.service.service.lib.association.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.associationreport.AssociationDetailBasicSituationRSP;
import cn.zswltech.mithras.service.mapper.model.associationreport.AssociationBasicSituation;
import cn.zswltech.mithras.service.mapper.model.associationreport.AssociationBasicSituationLib;
import cn.zswltech.mithras.service.service.lib.association.handler.AssociationReportLibAbstractHandler;
import org.springframework.stereotype.Service;

@Service
public class AssociationBasicSituationLibHandler extends AssociationReportLibAbstractHandler<AssociationBasicSituationLib, AssociationBasicSituation, AssociationDetailBasicSituationRSP> {
    @Override
    protected AssociationBasicSituationLib entity2Lib(AssociationBasicSituation f) {
        return BeanUtil.copyProperties(f, AssociationBasicSituationLib.class);
    }

    @Override
    protected AssociationBasicSituation lib2Entity(AssociationBasicSituationLib t) {
        return BeanUtil.copyProperties(t, AssociationBasicSituation.class);
    }

    @Override
    protected AssociationDetailBasicSituationRSP lib2Rsp(AssociationBasicSituationLib f) {
        AssociationDetailBasicSituationRSP rsp = BeanUtil.copyProperties(f, AssociationDetailBasicSituationRSP.class);
        return rsp;
    }


    @Override
    public boolean needHandle(Long clientId) {
        return true;
    }
}
