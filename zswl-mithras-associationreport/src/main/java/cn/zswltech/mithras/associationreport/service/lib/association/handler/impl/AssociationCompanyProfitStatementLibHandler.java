package cn.zswltech.mithras.associationreport.service.lib.association.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.associationreport.AssociationDetailProfitRSP;
import cn.zswltech.mithras.associationreport.mapper.model.AssociationCompanyProfitStatement;
import cn.zswltech.mithras.associationreport.mapper.model.AssociationCompanyProfitStatementLib;
import cn.zswltech.mithras.associationreport.service.lib.association.handler.AssociationReportLibAbstractHandler;
import org.springframework.stereotype.Service;

@Service
public class AssociationCompanyProfitStatementLibHandler extends AssociationReportLibAbstractHandler<AssociationCompanyProfitStatementLib, AssociationCompanyProfitStatement, AssociationDetailProfitRSP> {
    @Override
    protected AssociationCompanyProfitStatementLib entity2Lib(AssociationCompanyProfitStatement f) {
        return BeanUtil.copyProperties(f, AssociationCompanyProfitStatementLib.class);
    }

    @Override
    protected AssociationCompanyProfitStatement lib2Entity(AssociationCompanyProfitStatementLib t) {
        return BeanUtil.copyProperties(t, AssociationCompanyProfitStatement.class);
    }

    @Override
    protected AssociationDetailProfitRSP lib2Rsp(AssociationCompanyProfitStatementLib f) {
        AssociationDetailProfitRSP rsp = BeanUtil.copyProperties(f, AssociationDetailProfitRSP.class);
        return rsp;
    }


    @Override
    public boolean needHandle(Long clientId) {
        return true;
    }
}
