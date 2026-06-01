package cn.zswltech.mithras.service.service.lib.afterlease.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckReportCSRSP;
import cn.zswltech.mithras.service.mapper.model.afterlease.NewAfterLeaseCheckReportContent;
import cn.zswltech.mithras.service.mapper.model.afterlease.NewAfterLeaseCheckReportContentLib;
import cn.zswltech.mithras.service.mapper.model.afterlease.NewAfterLeaseCheckReportDetail;
import cn.zswltech.mithras.service.mapper.model.afterlease.NewAfterLeaseCheckReportDetailLib;
import cn.zswltech.mithras.service.service.lib.afterlease.handler.AfterLeaseCheckReportLibAbstractHandler;
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
