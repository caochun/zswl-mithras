package cn.zswltech.mithras.afterlease.application.lib.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckReportNonPublicExtraRSP;
import cn.zswltech.mithras.afterlease.mapper.model.NewAfterLeaseCheckReportExtra;
import cn.zswltech.mithras.afterlease.mapper.model.NewAfterLeaseCheckReportExtraLib;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.afterlease.application.lib.handler.AfterLeaseCheckReportLibAbstractHandler;
import org.springframework.stereotype.Component;

/**
 * @author dingqi
 * @date 2022/12/15
 * @description
 */
@Component
public class AfterLeaseCheckReportExtraLibHandler extends AfterLeaseCheckReportLibAbstractHandler<NewAfterLeaseCheckReportExtraLib, NewAfterLeaseCheckReportExtra, AfterLeaseCheckReportNonPublicExtraRSP> {
    @Override
    public boolean needHandle(Long mainId) {
        return true;
    }

    @Override
    protected NewAfterLeaseCheckReportExtraLib entity2Lib(NewAfterLeaseCheckReportExtra f) {
        return BeanUtil.copyProperties(f, NewAfterLeaseCheckReportExtraLib.class);
    }

    @Override
    protected NewAfterLeaseCheckReportExtra lib2Entity(NewAfterLeaseCheckReportExtraLib t) {
        return BeanUtil.copyProperties(t, NewAfterLeaseCheckReportExtra.class);
    }

    @Override
    protected AfterLeaseCheckReportNonPublicExtraRSP lib2Rsp(NewAfterLeaseCheckReportExtraLib f) {
        throw new MithrasException("暂不支持功能");
    }
}
