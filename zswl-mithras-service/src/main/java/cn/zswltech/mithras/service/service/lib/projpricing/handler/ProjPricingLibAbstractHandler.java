package cn.zswltech.mithras.service.service.lib.projpricing.handler;

import cn.zswltech.mithras.dto.ListBaseRSP;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.projpricing.ProjPricingInfoModule;
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
public abstract class ProjPricingLibAbstractHandler<LIB extends ILib, ENTITY extends IEntity, RSP extends ListBaseRSP>
        extends LibAbstractHandler<LIB, ENTITY, RSP> {

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
        return "project_id";
    }

    @Override
    public String entityMainIdFieldName() {
        return "project_id";
    }

    public void validateData(Client client) {
    }

    public abstract ProjPricingInfoModule getSubModule();

    public abstract boolean needHandle(Long mainId);

    @Override
    public Set<String> compareIgnoreFieldNames() {
        return Collections.EMPTY_SET;
    }

    @Override
    public BusinessModuleEnum businessModuleEnum() {
        return BusinessModuleEnum.PROJ_PRICING;
    }
}
