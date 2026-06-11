package cn.zswltech.mithras.afterlease.application.lib.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckReportCSRSP;
import cn.zswltech.mithras.afterlease.mapper.model.NewAfterLeaseCheckReportContent;
import cn.zswltech.mithras.afterlease.mapper.model.NewAfterLeaseCheckReportContentLib;
import cn.zswltech.mithras.afterlease.mapper.model.NewAfterLeaseCheckReportDetail;
import cn.zswltech.mithras.afterlease.mapper.model.NewAfterLeaseCheckReportDetailLib;
import cn.zswltech.mithras.afterlease.application.lib.handler.AfterLeaseCheckReportLibAbstractHandler;
import org.springframework.stereotype.Component;

/**
 * @author dingqi
 * @date 2023/11/15
 * @description
 */
@Component
public class NewAfterLeaseCheckReportDetailLibHandler extends AfterLeaseCheckReportLibAbstractHandler<NewAfterLeaseCheckReportDetailLib, NewAfterLeaseCheckReportDetail, AfterLeaseCheckReportCSRSP> {
    @Override
    public boolean needHandle(Long mainId) {
        return true;
    }

    @Override
    protected NewAfterLeaseCheckReportDetailLib entity2Lib(NewAfterLeaseCheckReportDetail f) {
        return BeanUtil.copyProperties(f, NewAfterLeaseCheckReportDetailLib.class);
    }

    @Override
    protected NewAfterLeaseCheckReportDetail lib2Entity(NewAfterLeaseCheckReportDetailLib t) {
        return BeanUtil.copyProperties(t, NewAfterLeaseCheckReportDetail.class);
    }

    @Override
    protected AfterLeaseCheckReportCSRSP lib2Rsp(NewAfterLeaseCheckReportDetailLib f) {
        throw new RuntimeException("暂未支持");
    }
}
