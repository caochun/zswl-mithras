package cn.zswltech.mithras.service.service.lib.association.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.associationreport.AssociationDetailLawInvolvedVisitRelatedInfoRSP;
import cn.zswltech.mithras.service.mapper.model.associationreport.AssociationLawInvolvedVisitRelatedInfo;
import cn.zswltech.mithras.service.mapper.model.associationreport.AssociationLawInvolvedVisitRelatedInfoLib;
import cn.zswltech.mithras.service.service.lib.association.handler.AssociationReportLibAbstractHandler;
import org.springframework.stereotype.Service;

@Service
public class AssociationLawInvolvedVisitRelatedInfoLibHandler extends AssociationReportLibAbstractHandler<AssociationLawInvolvedVisitRelatedInfoLib, AssociationLawInvolvedVisitRelatedInfo, AssociationDetailLawInvolvedVisitRelatedInfoRSP> {
    @Override
    protected AssociationLawInvolvedVisitRelatedInfoLib entity2Lib(AssociationLawInvolvedVisitRelatedInfo f) {
        return BeanUtil.copyProperties(f, AssociationLawInvolvedVisitRelatedInfoLib.class);
    }

    @Override
    protected AssociationLawInvolvedVisitRelatedInfo lib2Entity(AssociationLawInvolvedVisitRelatedInfoLib t) {
        return BeanUtil.copyProperties(t, AssociationLawInvolvedVisitRelatedInfo.class);
    }

    @Override
    protected AssociationDetailLawInvolvedVisitRelatedInfoRSP lib2Rsp(AssociationLawInvolvedVisitRelatedInfoLib f) {
        AssociationDetailLawInvolvedVisitRelatedInfoRSP rsp = BeanUtil.copyProperties(f, AssociationDetailLawInvolvedVisitRelatedInfoRSP.class);
        return rsp;
    }


    @Override
    public boolean needHandle(Long clientId) {
        return true;
    }
}
