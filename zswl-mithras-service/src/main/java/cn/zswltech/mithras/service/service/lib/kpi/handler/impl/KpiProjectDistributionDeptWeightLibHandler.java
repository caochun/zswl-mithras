package cn.zswltech.mithras.service.service.lib.kpi.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.kpi.KpiProjectDistributionDeptWeightInfo;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.mapper.model.kpi.KpiProjectDistributionDeptWeight;
import cn.zswltech.mithras.service.mapper.model.kpi.KpiProjectDistributionDeptWeightLib;
import cn.zswltech.mithras.service.service.Id2NameService;
import cn.zswltech.mithras.service.service.lib.kpi.handler.KpiProjectDistributionAbstractLibHandler;
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
    private Id2NameService id2NameService;


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
        info.setWeightTargetName(id2NameService.deptId2NameSingle(lib.getWeightTarget()));
        return info;
    }
}
