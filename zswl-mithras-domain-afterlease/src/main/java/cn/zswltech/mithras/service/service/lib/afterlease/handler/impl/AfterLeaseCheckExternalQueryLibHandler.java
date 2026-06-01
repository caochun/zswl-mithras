package cn.zswltech.mithras.service.service.lib.afterlease.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckExternalQueryListRsp;
import cn.zswltech.mithras.service.mapper.model.afterlease.NewAfterLeaseCheckExternalQuery;
import cn.zswltech.mithras.service.mapper.model.afterlease.NewAfterLeaseCheckExternalQueryLib;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.lib.afterlease.handler.AfterLeaseCheckExternalQueryLibAbstractHandler;
import org.springframework.stereotype.Component;

/**
 * @author dingqi
 * @date 2022/12/15
 * @description
 */
@Component
public class AfterLeaseCheckExternalQueryLibHandler extends AfterLeaseCheckExternalQueryLibAbstractHandler<NewAfterLeaseCheckExternalQueryLib, NewAfterLeaseCheckExternalQuery, AfterLeaseCheckExternalQueryListRsp> {
    @Override
    public boolean needHandle(Long mainId) {
        return true;
    }

    @Override
    protected NewAfterLeaseCheckExternalQueryLib entity2Lib(NewAfterLeaseCheckExternalQuery f) {
        return BeanUtil.copyProperties(f, NewAfterLeaseCheckExternalQueryLib.class);
    }

    @Override
    protected NewAfterLeaseCheckExternalQuery lib2Entity(NewAfterLeaseCheckExternalQueryLib t) {
        return BeanUtil.copyProperties(t, NewAfterLeaseCheckExternalQuery.class);
    }

    @Override
    protected AfterLeaseCheckExternalQueryListRsp lib2Rsp(NewAfterLeaseCheckExternalQueryLib f) {
        throw new MithrasException("暂不支持功能");
    }

    @Override
    public String libMainIdFieldName() {
        return "origin_id";
    }

    @Override
    public String entityMainIdFieldName() {
        return "id";
    }
}
