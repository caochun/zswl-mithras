package cn.zswltech.mithras.service.service.lib.afterlease.handler;

import cn.zswltech.mithras.dto.ListBaseRSP;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.afterlease.AfterLeaseCheckPlanLibModelEnum;
import cn.zswltech.mithras.service.mapper.tag.IEntity;
import cn.zswltech.mithras.service.mapper.tag.ILib;
import cn.zswltech.mithras.service.service.lib.LibAbstractHandler;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Set;

/**
 * @author dingqi
 * @date 2022/12/14
 * @description
 */
public abstract class AfterLeaseCheckReportLibAbstractHandler<LIB extends ILib, ENTITY extends IEntity, RSP extends ListBaseRSP> extends LibAbstractHandler<LIB, ENTITY, RSP> {
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void flushData(String version, Long checkPlanProjectId, boolean needClearLast, Integer versionType) {
        if (!needHandle(checkPlanProjectId)) {
            return;
        }
        super.flushData(version, checkPlanProjectId, needClearLast, versionType);
    }

    /**
     * 审批拒绝 把版本表最新的数据还原到 临时表
     *
     * @param checkPlanProjectId
     * @param version
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void reset(Long checkPlanProjectId, String version) {
        if (!needHandle(checkPlanProjectId)) {
            return;
        }
        super.reset(checkPlanProjectId, version);
    }

    @Override
    public String libMainIdFieldName() {
        return "check_plan_client_id";
    }

    @Override
    public String entityMainIdFieldName() {
        return "check_plan_client_id";
    }

    public abstract boolean needHandle(Long mainId);

    @Override
    public Set<String> compareIgnoreFieldNames() {
        return Collections.EMPTY_SET;
    }

    @Override
    public BusinessModuleEnum businessModuleEnum() {
        return BusinessModuleEnum.NEW_AFTER_LEASE_CHECK_REPORT;
    }
}
