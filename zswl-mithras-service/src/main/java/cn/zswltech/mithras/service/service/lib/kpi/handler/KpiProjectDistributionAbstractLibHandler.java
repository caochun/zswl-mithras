package cn.zswltech.mithras.service.service.lib.kpi.handler;

import cn.zswltech.mithras.dto.ListBaseRSP;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.mapper.tag.IEntity;
import cn.zswltech.mithras.service.mapper.tag.ILib;
import cn.zswltech.mithras.service.service.lib.LibAbstractHandler;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * @author dingqi
 * @date 2023/6/14
 * @description
 */
@Component
public abstract class KpiProjectDistributionAbstractLibHandler<LIB extends ILib, ENTITY extends IEntity, RSP extends ListBaseRSP> extends LibAbstractHandler<LIB, ENTITY, RSP> {
    @Override
    public String libMainIdFieldName() {
        return "project_distribution_id";
    }

    @Override
    public String entityMainIdFieldName() {
        return "project_distribution_id";
    }

    @Override
    public Set<String> compareIgnoreFieldNames() {
        return null;
    }

    @Override
    public BusinessModuleEnum businessModuleEnum() {
        return BusinessModuleEnum.KPI_PROJECT_DISTRIBUTION;
    }
}
