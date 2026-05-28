package cn.zswltech.mithras.service.service.lib.association.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.associationreport.AssociationDetailMajorMattersBasicReportRSP;
import cn.zswltech.mithras.service.mapper.model.associationreport.AssociationMajorMattersBasicReport;
import cn.zswltech.mithras.service.mapper.model.associationreport.AssociationMajorMattersBasicReportLib;
import cn.zswltech.mithras.service.service.lib.association.handler.AssociationReportLibAbstractHandler;
import org.springframework.stereotype.Service;

@Service
public class AssociationMajorMattersBasicReportLibHandler extends AssociationReportLibAbstractHandler<AssociationMajorMattersBasicReportLib, AssociationMajorMattersBasicReport, AssociationDetailMajorMattersBasicReportRSP> {
    @Override
    protected AssociationMajorMattersBasicReportLib entity2Lib(AssociationMajorMattersBasicReport f) {
        return BeanUtil.copyProperties(f, AssociationMajorMattersBasicReportLib.class);
    }

    @Override
    protected AssociationMajorMattersBasicReport lib2Entity(AssociationMajorMattersBasicReportLib t) {
        return BeanUtil.copyProperties(t, AssociationMajorMattersBasicReport.class);
    }

    @Override
    protected AssociationDetailMajorMattersBasicReportRSP lib2Rsp(AssociationMajorMattersBasicReportLib f) {
        AssociationDetailMajorMattersBasicReportRSP rsp = BeanUtil.copyProperties(f, AssociationDetailMajorMattersBasicReportRSP.class);
        return rsp;
    }


    @Override
    public boolean needHandle(Long clientId) {
        return true;
    }
}
