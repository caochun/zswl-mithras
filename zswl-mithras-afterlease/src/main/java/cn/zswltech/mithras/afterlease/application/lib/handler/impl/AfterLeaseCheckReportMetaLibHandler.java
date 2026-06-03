package cn.zswltech.mithras.afterlease.application.lib.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.ListBaseRSP;
import cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.model.NewAfterLeaseCheckReportMeta;
import cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.model.NewAfterLeaseCheckReportMetaLib;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.afterlease.application.lib.handler.AfterLeaseCheckReportLibAbstractHandler;
import org.springframework.stereotype.Component;

/**
 * @author dingqi
 * @date 2022/12/16
 * @description
 */
@Component
public class AfterLeaseCheckReportMetaLibHandler extends AfterLeaseCheckReportLibAbstractHandler<NewAfterLeaseCheckReportMetaLib, NewAfterLeaseCheckReportMeta, ListBaseRSP> {
    @Override
    public boolean needHandle(Long mainId) {
        return true;
    }

    @Override
    protected NewAfterLeaseCheckReportMetaLib entity2Lib(NewAfterLeaseCheckReportMeta f) {
        return BeanUtil.copyProperties(f, NewAfterLeaseCheckReportMetaLib.class);
    }

    @Override
    protected NewAfterLeaseCheckReportMeta lib2Entity(NewAfterLeaseCheckReportMetaLib t) {
        return BeanUtil.copyProperties(t, NewAfterLeaseCheckReportMeta.class);
    }

    @Override
    protected ListBaseRSP lib2Rsp(NewAfterLeaseCheckReportMetaLib f) {
        throw new MithrasException("暂不支持的功能");
    }
}
