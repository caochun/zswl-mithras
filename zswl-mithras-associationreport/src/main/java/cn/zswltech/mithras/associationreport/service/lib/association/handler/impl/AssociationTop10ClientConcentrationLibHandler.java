package cn.zswltech.mithras.associationreport.service.lib.association.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.associationreport.AssociationDetailTop10ClientConcentrationRSP;
import cn.zswltech.mithras.associationreport.mapper.model.AssociationTop10ClientConcentration;
import cn.zswltech.mithras.associationreport.mapper.model.AssociationTop10ClientConcentrationLib;
import cn.zswltech.mithras.associationreport.service.lib.association.handler.AssociationReportLibAbstractHandler;
import org.springframework.stereotype.Service;

@Service
public class AssociationTop10ClientConcentrationLibHandler extends AssociationReportLibAbstractHandler<AssociationTop10ClientConcentrationLib, AssociationTop10ClientConcentration, AssociationDetailTop10ClientConcentrationRSP> {
    @Override
    protected AssociationTop10ClientConcentrationLib entity2Lib(AssociationTop10ClientConcentration f) {
        return BeanUtil.copyProperties(f, AssociationTop10ClientConcentrationLib.class);
    }

    @Override
    protected AssociationTop10ClientConcentration lib2Entity(AssociationTop10ClientConcentrationLib t) {
        return BeanUtil.copyProperties(t, AssociationTop10ClientConcentration.class);
    }

    @Override
    protected AssociationDetailTop10ClientConcentrationRSP lib2Rsp(AssociationTop10ClientConcentrationLib f) {
        AssociationDetailTop10ClientConcentrationRSP rsp = BeanUtil.copyProperties(f, AssociationDetailTop10ClientConcentrationRSP.class);
        return rsp;
    }


    @Override
    public boolean needHandle(Long clientId) {
        return true;
    }
}
