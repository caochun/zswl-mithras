package cn.zswltech.mithras.service.service.lib.afterlease.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckClientListRSP;
import cn.zswltech.mithras.service.enums.afterlease.AfterLeaseCheckPlanLibModelEnum;
import cn.zswltech.mithras.service.mapper.model.afterlease.NewAfterLeaseCheckPlanClient;
import cn.zswltech.mithras.service.mapper.model.afterlease.NewAfterLeaseCheckPlanClientLib;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.lib.afterlease.handler.AfterLeaseCheckPlanLibAbstractHandler;
import org.springframework.stereotype.Component;

/**
 * @author dingqi
 * @date 2022/11/15
 * @description
 */
@Component
public class AfterLeaseCheckPlanClientLibHandler extends AfterLeaseCheckPlanLibAbstractHandler<NewAfterLeaseCheckPlanClientLib, NewAfterLeaseCheckPlanClient, AfterLeaseCheckClientListRSP> {
    @Override
    public AfterLeaseCheckPlanLibModelEnum getSubModule() {
        return AfterLeaseCheckPlanLibModelEnum.CLIENT;
    }

    @Override
    public boolean needHandle(Long mainId) {
        return true;
    }

    @Override
    protected NewAfterLeaseCheckPlanClientLib entity2Lib(NewAfterLeaseCheckPlanClient f) {
        return BeanUtil.copyProperties(f, NewAfterLeaseCheckPlanClientLib.class);
    }

    @Override
    protected NewAfterLeaseCheckPlanClient lib2Entity(NewAfterLeaseCheckPlanClientLib t) {
        return BeanUtil.copyProperties(t, NewAfterLeaseCheckPlanClient.class);
    }

    @Override
    protected AfterLeaseCheckClientListRSP lib2Rsp(NewAfterLeaseCheckPlanClientLib f) {
        throw new MithrasException("暂不支持功能");
    }
}
