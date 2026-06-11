package cn.zswltech.mithras.afterlease.application.lib.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckPlanDetailRSP;
import cn.zswltech.mithras.afterlease.enums.AfterLeaseCheckPlanLibModelEnum;
import cn.zswltech.mithras.afterlease.model.NewAfterLeaseCheckPlanBase;
import cn.zswltech.mithras.afterlease.model.NewAfterLeaseCheckPlanBaseLib;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.afterlease.application.lib.handler.AfterLeaseCheckPlanLibAbstractHandler;
import org.springframework.stereotype.Component;

/**
 * @author dingqi
 * @date 2022/11/15
 * @description
 */
@Component
public class AfterLeaseCheckPlanBaseLibHandler extends AfterLeaseCheckPlanLibAbstractHandler<NewAfterLeaseCheckPlanBaseLib, NewAfterLeaseCheckPlanBase, AfterLeaseCheckPlanDetailRSP> {
    @Override
    public AfterLeaseCheckPlanLibModelEnum getSubModule() {
        return AfterLeaseCheckPlanLibModelEnum.BASE;
    }

    @Override
    public boolean needHandle(Long mainId) {
        return true;
    }

    @Override
    protected NewAfterLeaseCheckPlanBaseLib entity2Lib(NewAfterLeaseCheckPlanBase f) {
        return BeanUtil.copyProperties(f, NewAfterLeaseCheckPlanBaseLib.class);
    }

    @Override
    protected NewAfterLeaseCheckPlanBase lib2Entity(NewAfterLeaseCheckPlanBaseLib t) {
        return BeanUtil.copyProperties(t, NewAfterLeaseCheckPlanBase.class);
    }

    @Override
    protected AfterLeaseCheckPlanDetailRSP lib2Rsp(NewAfterLeaseCheckPlanBaseLib f) {
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
