package cn.zswltech.mithras.service.service.lib.afterlease.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckReportBaseRSP;
import cn.zswltech.mithras.service.mapper.model.afterlease.NewAfterLeaseCheckReportBase;
import cn.zswltech.mithras.service.mapper.model.afterlease.NewAfterLeaseCheckReportBaseLib;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.lib.afterlease.handler.AfterLeaseCheckReportLibAbstractHandler;
import org.springframework.stereotype.Component;

/**
 * @author dingqi
 * @date 2022/12/14
 * @description
 */
@Component
public class AfterLeaseCheckReportBaseLibHandler extends AfterLeaseCheckReportLibAbstractHandler<NewAfterLeaseCheckReportBaseLib, NewAfterLeaseCheckReportBase, AfterLeaseCheckReportBaseRSP> {
    @Override
    public boolean needHandle(Long mainId) {
        return true;
    }

    @Override
    protected NewAfterLeaseCheckReportBaseLib entity2Lib(NewAfterLeaseCheckReportBase f) {
        return BeanUtil.copyProperties(f, NewAfterLeaseCheckReportBaseLib.class);
    }

    @Override
    protected NewAfterLeaseCheckReportBase lib2Entity(NewAfterLeaseCheckReportBaseLib t) {
        return BeanUtil.copyProperties(t, NewAfterLeaseCheckReportBase.class);
    }

    @Override
    protected AfterLeaseCheckReportBaseRSP lib2Rsp(NewAfterLeaseCheckReportBaseLib f) {
        throw new MithrasException("暂不支持功能");
    }
}
