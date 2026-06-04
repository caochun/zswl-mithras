package cn.zswltech.mithras.associationreport.service.lib.association.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.associationreport.AssociationDetailBusinessSituationRSP;
import cn.zswltech.mithras.associationreport.mapper.model.AssociationBusinessSituation;
import cn.zswltech.mithras.associationreport.mapper.model.AssociationBusinessSituationLib;
import cn.zswltech.mithras.associationreport.service.lib.association.handler.AssociationReportLibAbstractHandler;
import org.springframework.stereotype.Service;

@Service
public class AssociationBusinessSituationLibHandler extends AssociationReportLibAbstractHandler<AssociationBusinessSituationLib, AssociationBusinessSituation, AssociationDetailBusinessSituationRSP> {
    @Override
    protected AssociationBusinessSituationLib entity2Lib(AssociationBusinessSituation f) {
        return BeanUtil.copyProperties(f, AssociationBusinessSituationLib.class);
    }

    @Override
    protected AssociationBusinessSituation lib2Entity(AssociationBusinessSituationLib t) {
        return BeanUtil.copyProperties(t, AssociationBusinessSituation.class);
    }

    @Override
    protected AssociationDetailBusinessSituationRSP lib2Rsp(AssociationBusinessSituationLib f) {
        AssociationDetailBusinessSituationRSP rsp = BeanUtil.copyProperties(f, AssociationDetailBusinessSituationRSP.class);
        return rsp;
    }


    @Override
    public boolean needHandle(Long clientId) {
        return true;
    }
}
