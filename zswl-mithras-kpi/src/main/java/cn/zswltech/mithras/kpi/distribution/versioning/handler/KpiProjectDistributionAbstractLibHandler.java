package cn.zswltech.mithras.kpi.distribution.versioning.handler;

import cn.zswltech.mithras.dto.ListBaseRSP;
import cn.zswltech.mithras.foundation.persistence.tag.IEntity;
import cn.zswltech.mithras.foundation.persistence.tag.ILib;
import cn.zswltech.mithras.foundation.version.LibAbstractHandler;
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
    protected String businessModuleName() {
        return "KPI_PROJECT_DISTRIBUTION";
    }
}
