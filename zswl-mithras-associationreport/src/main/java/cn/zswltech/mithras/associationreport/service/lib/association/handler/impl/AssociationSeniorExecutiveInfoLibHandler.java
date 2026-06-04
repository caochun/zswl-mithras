package cn.zswltech.mithras.associationreport.service.lib.association.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.associationreport.AssociationDetailSeniorExecutiveInfoRSP;
import cn.zswltech.mithras.associationreport.mapper.model.AssociationSeniorExecutiveInfo;
import cn.zswltech.mithras.associationreport.mapper.model.AssociationSeniorExecutiveInfoLib;
import cn.zswltech.mithras.associationreport.service.lib.association.handler.AssociationReportLibAbstractHandler;
import org.springframework.stereotype.Service;

@Service
public class AssociationSeniorExecutiveInfoLibHandler extends AssociationReportLibAbstractHandler<AssociationSeniorExecutiveInfoLib, AssociationSeniorExecutiveInfo, AssociationDetailSeniorExecutiveInfoRSP> {
    @Override
    protected AssociationSeniorExecutiveInfoLib entity2Lib(AssociationSeniorExecutiveInfo f) {
        return BeanUtil.copyProperties(f, AssociationSeniorExecutiveInfoLib.class);
    }

    @Override
    protected AssociationSeniorExecutiveInfo lib2Entity(AssociationSeniorExecutiveInfoLib t) {
        return BeanUtil.copyProperties(t, AssociationSeniorExecutiveInfo.class);
    }

    @Override
    protected AssociationDetailSeniorExecutiveInfoRSP lib2Rsp(AssociationSeniorExecutiveInfoLib f) {
        AssociationDetailSeniorExecutiveInfoRSP rsp = BeanUtil.copyProperties(f, AssociationDetailSeniorExecutiveInfoRSP.class);
        return rsp;
    }


    @Override
    public boolean needHandle(Long clientId) {
        return true;
    }
}
