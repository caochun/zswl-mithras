package cn.zswltech.mithras.kpi.application.distribution.lib.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.ListBaseRSP;
import cn.zswltech.mithras.kpi.mapper.model.KpiProjectDistributionBaseInfo;
import cn.zswltech.mithras.kpi.mapper.model.KpiProjectDistributionBaseInfoLib;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.kpi.application.distribution.lib.handler.KpiProjectDistributionAbstractLibHandler;
import org.springframework.stereotype.Component;

/**
 * @author dingqi
 * @date 2023/6/14
 * @description
 */
@Component
public class KpiProjectDistributionBaseInfoLibHandler extends KpiProjectDistributionAbstractLibHandler<KpiProjectDistributionBaseInfoLib, KpiProjectDistributionBaseInfo, ListBaseRSP> {
    @Override
    protected KpiProjectDistributionBaseInfoLib entity2Lib(KpiProjectDistributionBaseInfo f) {
        return BeanUtil.copyProperties(f, KpiProjectDistributionBaseInfoLib.class);
    }

    @Override
    protected KpiProjectDistributionBaseInfo lib2Entity(KpiProjectDistributionBaseInfoLib t) {
        return BeanUtil.copyProperties(t, KpiProjectDistributionBaseInfo.class);
    }

    @Override
    protected ListBaseRSP lib2Rsp(KpiProjectDistributionBaseInfoLib f) {
        throw new MithrasException("暂不支持的功能");
    }
}
