package cn.zswltech.mithras.afterlease.application.lib.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckExternalQueryClientInfoListRsp;
import cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.model.NewAfterLeaseCheckExternalQueryClientInfo;
import cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.model.NewAfterLeaseCheckExternalQueryClientInfoLib;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.afterlease.application.lib.handler.AfterLeaseCheckExternalQueryLibAbstractHandler;
import org.springframework.stereotype.Component;

/**
 * @author dingqi
 * @date 2022/12/15
 * @description
 */
@Component
public class AfterLeaseCheckExternalQueryClientInfoLibHandler extends AfterLeaseCheckExternalQueryLibAbstractHandler<NewAfterLeaseCheckExternalQueryClientInfoLib, NewAfterLeaseCheckExternalQueryClientInfo, AfterLeaseCheckExternalQueryClientInfoListRsp> {
    @Override
    public boolean needHandle(Long mainId) {
        return true;
    }

    @Override
    protected NewAfterLeaseCheckExternalQueryClientInfoLib entity2Lib(NewAfterLeaseCheckExternalQueryClientInfo f) {
        return BeanUtil.copyProperties(f, NewAfterLeaseCheckExternalQueryClientInfoLib.class);
    }

    @Override
    protected NewAfterLeaseCheckExternalQueryClientInfo lib2Entity(NewAfterLeaseCheckExternalQueryClientInfoLib t) {
        return BeanUtil.copyProperties(t, NewAfterLeaseCheckExternalQueryClientInfo.class);
    }

    @Override
    protected AfterLeaseCheckExternalQueryClientInfoListRsp lib2Rsp(NewAfterLeaseCheckExternalQueryClientInfoLib f) {
        throw new MithrasException("暂不支持功能");
    }
}
