package cn.zswltech.mithras.kpi.application.distribution.lib.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.kpi.KpiProjectDistributionDeptLaunchWeightInfo;
import cn.zswltech.mithras.kpi.mapper.model.KpiProjectDistributionDeptLaunchWeight;
import cn.zswltech.mithras.kpi.mapper.model.KpiProjectDistributionDeptLaunchWeightLib;
import cn.zswltech.mithras.kpi.application.distribution.lib.handler.KpiProjectDistributionAbstractLibHandler;
import cn.zswltech.mithras.foundation.port.DeptNameResolver;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author dingqi
 * @date 2023/6/14
 * @description
 */
@Component
public class KpiProjectDistributionDeptLaunchWeightLibHandler extends KpiProjectDistributionAbstractLibHandler<KpiProjectDistributionDeptLaunchWeightLib, KpiProjectDistributionDeptLaunchWeight, KpiProjectDistributionDeptLaunchWeightInfo> {
    @Resource
    private DeptNameResolver deptNameResolver;


    @Override
    protected KpiProjectDistributionDeptLaunchWeightLib entity2Lib(KpiProjectDistributionDeptLaunchWeight f) {
        return BeanUtil.copyProperties(f, KpiProjectDistributionDeptLaunchWeightLib.class);
    }

    @Override
    protected KpiProjectDistributionDeptLaunchWeight lib2Entity(KpiProjectDistributionDeptLaunchWeightLib t) {
        return BeanUtil.copyProperties(t, KpiProjectDistributionDeptLaunchWeight.class);
    }

    @Override
    protected KpiProjectDistributionDeptLaunchWeightInfo lib2Rsp(KpiProjectDistributionDeptLaunchWeightLib lib) {
        KpiProjectDistributionDeptLaunchWeightInfo info = new KpiProjectDistributionDeptLaunchWeightInfo();
        info.setId(lib.getId());
        info.setWeightValue(lib.getWeightValue());
        info.setWeightTarget(lib.getWeightTarget());
        info.setWeightTargetName(deptNameResolver.deptId2NameSingle(lib.getWeightTarget()));
        return info;
    }
}
