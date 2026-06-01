package cn.zswltech.mithras.service.service.lib.association.handler;

import cn.zswltech.mithras.dto.ListBaseRSP;
import cn.zswltech.mithras.service.mapper.tag.IEntity;
import cn.zswltech.mithras.service.mapper.tag.ILib;
import cn.zswltech.mithras.service.service.lib.LibAbstractHandler;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Set;

/**
 * 客户版本处理器
 *
 * @author wangchuanhao
 * @date 2022/7/19 10:49 PM
 */
public abstract class AssociationReportLibAbstractHandler<LIB extends ILib, ENTITY extends IEntity, RSP extends ListBaseRSP>
        extends LibAbstractHandler<LIB, ENTITY, RSP> {

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void flushData(String version, Long reportInstanceId, boolean needClearLast, Integer versionType) {
        if (!needHandle(reportInstanceId)) {
            return;
        }
        super.flushData(version, reportInstanceId, needClearLast, versionType);
    }

    /**
     * 审批拒绝 把版本表最新的数据还原到 临时表
     *
     * @param reportInstanceId
     * @param version
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void reset(Long reportInstanceId, String version) {
        if (!needHandle(reportInstanceId)) {
            return;
        }
        super.reset(reportInstanceId, version);
    }

    @Override
    public String libMainIdFieldName() {
        return "report_instance_id";
    }

    @Override
    public String entityMainIdFieldName() {
        return "report_instance_id";
    }

    public abstract boolean needHandle(Long mainId);

    @Override
    public Set<String> compareIgnoreFieldNames() {
        return Collections.EMPTY_SET;
    }

    @Override
    protected String businessModuleName() {
        return "ASSOCIATION_REPORT_APPLY";
    }
}
