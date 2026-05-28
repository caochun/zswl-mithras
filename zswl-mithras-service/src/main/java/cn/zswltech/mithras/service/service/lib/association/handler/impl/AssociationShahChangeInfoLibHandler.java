package cn.zswltech.mithras.service.service.lib.association.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.associationreport.AssociationDetailShahChangeInfoRSP;
import cn.zswltech.mithras.service.mapper.model.associationreport.AssociationShahChangeInfo;
import cn.zswltech.mithras.service.mapper.model.associationreport.AssociationShahChangeInfoLib;
import cn.zswltech.mithras.service.service.lib.association.handler.AssociationReportLibAbstractHandler;
import org.springframework.stereotype.Service;

@Service
public class AssociationShahChangeInfoLibHandler extends AssociationReportLibAbstractHandler<AssociationShahChangeInfoLib, AssociationShahChangeInfo, AssociationDetailShahChangeInfoRSP> {
    @Override
    protected AssociationShahChangeInfoLib entity2Lib(AssociationShahChangeInfo f) {
        return BeanUtil.copyProperties(f, AssociationShahChangeInfoLib.class);
    }

    @Override
    protected AssociationShahChangeInfo lib2Entity(AssociationShahChangeInfoLib t) {
        return BeanUtil.copyProperties(t, AssociationShahChangeInfo.class);
    }

    @Override
    protected AssociationDetailShahChangeInfoRSP lib2Rsp(AssociationShahChangeInfoLib f) {
        AssociationDetailShahChangeInfoRSP rsp = BeanUtil.copyProperties(f, AssociationDetailShahChangeInfoRSP.class);
        return rsp;
    }


    @Override
    public boolean needHandle(Long clientId) {
        return true;
    }
}
