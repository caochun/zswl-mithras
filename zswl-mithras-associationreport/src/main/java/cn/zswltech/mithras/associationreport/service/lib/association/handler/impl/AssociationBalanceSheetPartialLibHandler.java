package cn.zswltech.mithras.associationreport.service.lib.association.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.associationreport.AssociationDetailBalanceSheetPartialRSP;
import cn.zswltech.mithras.associationreport.mapper.model.AssociationBalanceSheetPartial;
import cn.zswltech.mithras.associationreport.mapper.model.AssociationBalanceSheetPartialLib;
import cn.zswltech.mithras.associationreport.service.lib.association.handler.AssociationReportLibAbstractHandler;
import org.springframework.stereotype.Service;

@Service
public class AssociationBalanceSheetPartialLibHandler extends AssociationReportLibAbstractHandler<AssociationBalanceSheetPartialLib, AssociationBalanceSheetPartial, AssociationDetailBalanceSheetPartialRSP> {
    @Override
    protected AssociationBalanceSheetPartialLib entity2Lib(AssociationBalanceSheetPartial f) {
        return BeanUtil.copyProperties(f, AssociationBalanceSheetPartialLib.class);
    }

    @Override
    protected AssociationBalanceSheetPartial lib2Entity(AssociationBalanceSheetPartialLib t) {
        return BeanUtil.copyProperties(t, AssociationBalanceSheetPartial.class);
    }

    @Override
    protected AssociationDetailBalanceSheetPartialRSP lib2Rsp(AssociationBalanceSheetPartialLib f) {
        AssociationDetailBalanceSheetPartialRSP rsp = BeanUtil.copyProperties(f, AssociationDetailBalanceSheetPartialRSP.class);
        return rsp;
    }


    @Override
    public boolean needHandle(Long clientId) {
        return true;
    }
}
