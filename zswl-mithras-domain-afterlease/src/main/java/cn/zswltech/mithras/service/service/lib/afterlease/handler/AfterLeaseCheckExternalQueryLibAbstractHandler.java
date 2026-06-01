package cn.zswltech.mithras.service.service.lib.afterlease.handler;

import cn.zswltech.mithras.dto.ListBaseRSP;
import cn.zswltech.mithras.service.mapper.tag.IEntity;
import cn.zswltech.mithras.service.mapper.tag.ILib;
import cn.zswltech.mithras.service.service.lib.LibAbstractHandler;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Set;

/**
 * @author dingqi
 * @date 2022/12/15
 * @description
 */
public abstract class AfterLeaseCheckExternalQueryLibAbstractHandler<LIB extends ILib, ENTITY extends IEntity, RSP extends ListBaseRSP> extends LibAbstractHandler<LIB, ENTITY, RSP> {
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void flushData(String version, Long queryId, boolean needClearLast, Integer versionType) {
        if (!needHandle(queryId)) {
            return;
        }
        super.flushData(version, queryId, needClearLast, versionType);
    }

    /**
     * 审批拒绝 把版本表最新的数据还原到 临时表
     *
     * @param queryId
     * @param version
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void reset(Long queryId, String version) {
        if (!needHandle(queryId)) {
            return;
        }
        super.reset(queryId, version);
    }

    @Override
    public String libMainIdFieldName() {
        return "query_id";
    }

    @Override
    public String entityMainIdFieldName() {
        return "query_id";
    }

    public abstract boolean needHandle(Long mainId);

    @Override
    public Set<String> compareIgnoreFieldNames() {
        return Collections.EMPTY_SET;
    }

    @Override
    protected String businessModuleName() {
        return "NEW_AFTER_LEASE_CHECK_EXTERNAL_QUERY";
    }
}
