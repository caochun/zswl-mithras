package cn.zswltech.mithras.service.service.lib.afterlease.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckReportCSRSP;
import cn.zswltech.mithras.service.mapper.model.afterlease.NewAfterLeaseCheckReportSummary;
import cn.zswltech.mithras.service.mapper.model.afterlease.NewAfterLeaseCheckReportSummaryLib;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.lib.afterlease.handler.AfterLeaseCheckReportLibAbstractHandler;
import org.springframework.stereotype.Component;

/**
 * @author dingqi
 * @date 2022/12/14
 * @description
 */
@Component
public class AfterLeaseCheckReportSummaryLibHandler extends AfterLeaseCheckReportLibAbstractHandler<NewAfterLeaseCheckReportSummaryLib, NewAfterLeaseCheckReportSummary, AfterLeaseCheckReportCSRSP> {
    @Override
    public boolean needHandle(Long mainId) {
        return true;
    }

    @Override
    protected NewAfterLeaseCheckReportSummaryLib entity2Lib(NewAfterLeaseCheckReportSummary f) {
        return BeanUtil.copyProperties(f, NewAfterLeaseCheckReportSummaryLib.class);
    }

    @Override
    protected NewAfterLeaseCheckReportSummary lib2Entity(NewAfterLeaseCheckReportSummaryLib t) {
        return BeanUtil.copyProperties(t, NewAfterLeaseCheckReportSummary.class);
    }

    @Override
    protected AfterLeaseCheckReportCSRSP lib2Rsp(NewAfterLeaseCheckReportSummaryLib f) {
        throw new MithrasException("暂不支持功能");
    }
}
