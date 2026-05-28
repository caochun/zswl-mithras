package cn.zswltech.mithras.service.service.lib.association.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.associationreport.AssociationDetailShahStorInfoRSP;
import cn.zswltech.mithras.service.mapper.model.associationreport.AssociationShahStorInfo;
import cn.zswltech.mithras.service.mapper.model.associationreport.AssociationShahStorInfoLib;
import cn.zswltech.mithras.service.service.lib.association.handler.AssociationReportLibAbstractHandler;
import org.springframework.stereotype.Service;

@Service
public class AssociationShahStorInfoLibHandler extends AssociationReportLibAbstractHandler<AssociationShahStorInfoLib, AssociationShahStorInfo, AssociationDetailShahStorInfoRSP> {
    @Override
    protected AssociationShahStorInfoLib entity2Lib(AssociationShahStorInfo f) {
        return BeanUtil.copyProperties(f, AssociationShahStorInfoLib.class);
    }

    @Override
    protected AssociationShahStorInfo lib2Entity(AssociationShahStorInfoLib t) {
        return BeanUtil.copyProperties(t, AssociationShahStorInfo.class);
    }

    @Override
    protected AssociationDetailShahStorInfoRSP lib2Rsp(AssociationShahStorInfoLib f) {
        AssociationDetailShahStorInfoRSP rsp = BeanUtil.copyProperties(f, AssociationDetailShahStorInfoRSP.class);
        return rsp;
    }


    @Override
    public boolean needHandle(Long clientId) {
        return true;
    }
}
