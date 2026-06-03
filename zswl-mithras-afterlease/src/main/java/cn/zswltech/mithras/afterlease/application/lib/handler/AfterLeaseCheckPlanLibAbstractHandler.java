package cn.zswltech.mithras.afterlease.application.lib.handler;

import cn.zswltech.mithras.dto.ListBaseRSP;
import cn.zswltech.mithras.afterlease.domain.enums.AfterLeaseCheckPlanLibModelEnum;
import cn.zswltech.mithras.service.mapper.tag.IEntity;
import cn.zswltech.mithras.service.mapper.tag.ILib;
import cn.zswltech.mithras.service.service.lib.LibAbstractHandler;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Set;

/**
 * @author dingqi
 * @date 2022/11/15
 * @description
 */
public abstract class AfterLeaseCheckPlanLibAbstractHandler<LIB extends ILib, ENTITY extends IEntity, RSP extends ListBaseRSP> extends LibAbstractHandler<LIB, ENTITY, RSP> {
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void flushData(String version, Long projReviewId, boolean needClearLast, Integer versionType) {
        if (!needHandle(projReviewId)) {
            return;
        }
        super.flushData(version, projReviewId, needClearLast, versionType);
    }

    /**
     * 审批拒绝 把版本表最新的数据还原到 临时表
     *
     * @param projReviewId
     * @param version
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void reset(Long projReviewId, String version) {
        if (!needHandle(projReviewId)) {
            return;
        }
        super.reset(projReviewId, version);
    }

    @Override
    public String libMainIdFieldName() {
        return "plan_id";
    }

    @Override
    public String entityMainIdFieldName() {
        return "plan_id";
    }

    public abstract AfterLeaseCheckPlanLibModelEnum getSubModule();

    public abstract boolean needHandle(Long mainId);

    @Override
    public Set<String> compareIgnoreFieldNames() {
        return Collections.EMPTY_SET;
    }

    @Override
    protected String businessModuleName() {
        return "NEW_AFTER_LEASE_CHECK_PLAN";
    }
}
