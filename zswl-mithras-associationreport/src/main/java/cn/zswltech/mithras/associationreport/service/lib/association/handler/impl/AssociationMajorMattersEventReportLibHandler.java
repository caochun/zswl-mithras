package cn.zswltech.mithras.associationreport.service.lib.association.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.associationreport.AssociationDetailMajorMattersEventReportRSP;
import cn.zswltech.mithras.associationreport.mapper.model.AssociationMajorMattersEventReport;
import cn.zswltech.mithras.associationreport.mapper.model.AssociationMajorMattersEventReportLib;
import cn.zswltech.mithras.associationreport.service.lib.association.handler.AssociationReportLibAbstractHandler;
import org.springframework.stereotype.Service;

@Service
public class AssociationMajorMattersEventReportLibHandler extends AssociationReportLibAbstractHandler<AssociationMajorMattersEventReportLib, AssociationMajorMattersEventReport, AssociationDetailMajorMattersEventReportRSP> {
    @Override
    protected AssociationMajorMattersEventReportLib entity2Lib(AssociationMajorMattersEventReport f) {
        return BeanUtil.copyProperties(f, AssociationMajorMattersEventReportLib.class);
    }

    @Override
    protected AssociationMajorMattersEventReport lib2Entity(AssociationMajorMattersEventReportLib t) {
        return BeanUtil.copyProperties(t, AssociationMajorMattersEventReport.class);
    }

    @Override
    protected AssociationDetailMajorMattersEventReportRSP lib2Rsp(AssociationMajorMattersEventReportLib f) {
        AssociationDetailMajorMattersEventReportRSP rsp = BeanUtil.copyProperties(f, AssociationDetailMajorMattersEventReportRSP.class);
        return rsp;
    }


    @Override
    public boolean needHandle(Long clientId) {
        return true;
    }
}
