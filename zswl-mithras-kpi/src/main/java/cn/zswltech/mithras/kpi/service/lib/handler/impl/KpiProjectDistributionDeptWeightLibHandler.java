package cn.zswltech.mithras.kpi.service.lib.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.kpi.KpiProjectDistributionDeptWeightInfo;
import cn.zswltech.mithras.kpi.mapper.model.KpiProjectDistributionDeptWeight;
import cn.zswltech.mithras.kpi.mapper.model.KpiProjectDistributionDeptWeightLib;
import cn.zswltech.mithras.kpi.service.lib.handler.KpiProjectDistributionAbstractLibHandler;
import cn.zswltech.mithras.service.service.DeptNameResolver;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author dingqi
 * @date 2023/6/14
 * @description
 */
@Component
public class KpiProjectDistributionDeptWeightLibHandler extends KpiProjectDistributionAbstractLibHandler<KpiProjectDistributionDeptWeightLib, KpiProjectDistributionDeptWeight, KpiProjectDistributionDeptWeightInfo> {
    @Resource
    private DeptNameResolver deptNameResolver;


    @Override
    protected KpiProjectDistributionDeptWeightLib entity2Lib(KpiProjectDistributionDeptWeight f) {
        return BeanUtil.copyProperties(f, KpiProjectDistributionDeptWeightLib.class);
    }

    @Override
    protected KpiProjectDistributionDeptWeight lib2Entity(KpiProjectDistributionDeptWeightLib t) {
        return BeanUtil.copyProperties(t, KpiProjectDistributionDeptWeight.class);
    }

    @Override
    protected KpiProjectDistributionDeptWeightInfo lib2Rsp(KpiProjectDistributionDeptWeightLib lib) {
        KpiProjectDistributionDeptWeightInfo info = new KpiProjectDistributionDeptWeightInfo();
        info.setId(lib.getId());
        info.setWeightValue(lib.getWeightValue());
        info.setWeightTarget(lib.getWeightTarget());
        info.setWeightTargetName(deptNameResolver.deptId2NameSingle(lib.getWeightTarget()));
        return info;
    }
}
