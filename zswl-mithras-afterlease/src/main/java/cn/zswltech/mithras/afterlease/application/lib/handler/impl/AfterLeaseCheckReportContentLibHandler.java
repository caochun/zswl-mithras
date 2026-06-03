package cn.zswltech.mithras.afterlease.application.lib.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckReportCSRSP;
import cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.model.NewAfterLeaseCheckReportContent;
import cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.model.NewAfterLeaseCheckReportContentLib;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.afterlease.application.lib.handler.AfterLeaseCheckReportLibAbstractHandler;
import org.springframework.stereotype.Component;

/**
 * @author dingqi
 * @date 2022/12/14
 * @description
 */
@Component
public class AfterLeaseCheckReportContentLibHandler extends AfterLeaseCheckReportLibAbstractHandler<NewAfterLeaseCheckReportContentLib, NewAfterLeaseCheckReportContent, AfterLeaseCheckReportCSRSP> {
    @Override
    public boolean needHandle(Long mainId) {
        return true;
    }

    @Override
    protected NewAfterLeaseCheckReportContentLib entity2Lib(NewAfterLeaseCheckReportContent f) {
        return BeanUtil.copyProperties(f, NewAfterLeaseCheckReportContentLib.class);
    }

    @Override
    protected NewAfterLeaseCheckReportContent lib2Entity(NewAfterLeaseCheckReportContentLib t) {
        return BeanUtil.copyProperties(t, NewAfterLeaseCheckReportContent.class);
    }

    @Override
    protected AfterLeaseCheckReportCSRSP lib2Rsp(NewAfterLeaseCheckReportContentLib f) {
        throw new MithrasException("暂不支持功能");
    }
}
