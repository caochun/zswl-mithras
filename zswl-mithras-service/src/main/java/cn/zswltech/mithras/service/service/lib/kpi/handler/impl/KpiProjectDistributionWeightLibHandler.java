package cn.zswltech.mithras.service.service.lib.kpi.handler.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.kpi.KpiProjectDistributionWeightInfo;
import cn.zswltech.mithras.kpi.enums.KpiProjectWeightTypeEnum;
import cn.zswltech.mithras.kpi.mapper.model.KpiProjectDistributionWeight;
import cn.zswltech.mithras.kpi.mapper.model.KpiProjectDistributionWeightLib;
import cn.zswltech.mithras.system.service.Id2NameService;
import cn.zswltech.mithras.kpi.service.lib.handler.KpiProjectDistributionAbstractLibHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * @author dingqi
 * @date 2023/6/14
 * @description
 */
@Component
public class KpiProjectDistributionWeightLibHandler extends KpiProjectDistributionAbstractLibHandler<KpiProjectDistributionWeightLib, KpiProjectDistributionWeight, KpiProjectDistributionWeightInfo> {
    @Resource
    private Id2NameService id2NameService;

    @Override
    protected KpiProjectDistributionWeightLib entity2Lib(KpiProjectDistributionWeight f) {
        return BeanUtil.copyProperties(f, KpiProjectDistributionWeightLib.class);
    }

    @Override
    public Set<String> compareIgnoreFieldNames() {
        HashSet<String> fields = new HashSet<>();
        fields.add("projectDistributionRecordId");
        fields.add("weightType");
        return fields;
    }

    @Override
    protected KpiProjectDistributionWeight lib2Entity(KpiProjectDistributionWeightLib t) {
        return BeanUtil.copyProperties(t, KpiProjectDistributionWeight.class);
    }

    @Override
    protected KpiProjectDistributionWeightInfo lib2Rsp(KpiProjectDistributionWeightLib f) {
        KpiProjectDistributionWeightInfo rsp = BeanUtil.copyProperties(f, KpiProjectDistributionWeightInfo.class);
        rsp.setWeightTypeName(Optional.ofNullable(KpiProjectWeightTypeEnum.find(rsp.getWeightType())).map(KpiProjectWeightTypeEnum::getDisplay).orElse(null));

        if(ObjectUtil.equals(rsp.getWeightType(), KpiProjectWeightTypeEnum.BUSINESS_DEPT.name())) {
            rsp.setWeightTargetName(id2NameService.deptId2NameSingle(rsp.getWeightTarget()));
        } else {
            rsp.setWeightTargetName(id2NameService.sysUserId2NameSingle(rsp.getWeightTarget()));
        }
        return rsp;
    }
}
