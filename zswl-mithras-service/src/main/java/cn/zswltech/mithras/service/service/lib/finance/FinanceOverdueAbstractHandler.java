package cn.zswltech.mithras.service.service.lib.finance;

import cn.zswltech.mithras.dto.ListBaseRSP;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.projreview.FinanceOverdueModule;
import cn.zswltech.mithras.service.mapper.model.client.Client;
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
public abstract class FinanceOverdueAbstractHandler<LIB extends ILib, ENTITY extends IEntity, RSP extends ListBaseRSP>
        extends LibAbstractHandler<LIB, ENTITY, RSP> {

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void flushData(String version, Long overdueReportId, boolean needClearLast, Integer versionType) {
        if (!needHandle(overdueReportId)) {
            return;
        }
        super.flushData(version, overdueReportId, needClearLast, versionType);
    }

    /**
     * 审批拒绝 把版本表最新的数据还原到 临时表
     *
     * @param overdueReportId
     * @param version
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void reset(Long overdueReportId, String version) {
        if (!needHandle(overdueReportId)) {
            return;
        }
        super.reset(overdueReportId, version);
    }

    @Override
    public String libMainIdFieldName() {
        return "overdue_report_id";
    }

    @Override
    public String entityMainIdFieldName() {
        return "overdue_report_id";
    }

    public void validateData(Client client) {
    }

    public abstract FinanceOverdueModule getSubModule();

    public abstract boolean needHandle(Long mainId);

    @Override
    public Set<String> compareIgnoreFieldNames() {
        return Collections.EMPTY_SET;
    }

    @Override
    public BusinessModuleEnum businessModuleEnum() {
        return BusinessModuleEnum.PROJ_REVIEW;
    }
}
