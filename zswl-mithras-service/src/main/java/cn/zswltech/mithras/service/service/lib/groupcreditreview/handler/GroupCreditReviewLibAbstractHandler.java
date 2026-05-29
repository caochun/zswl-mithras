package cn.zswltech.mithras.service.service.lib.groupcreditreview.handler;

import cn.zswltech.mithras.dto.ListBaseRSP;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.groupcreditreview.GroupCreditReviewInfoModule;
import cn.zswltech.mithras.service.enums.projestablish.ProjEstablishInfoModule;
import cn.zswltech.mithras.common.model.IEntity;
import cn.zswltech.mithras.service.mapper.tag.ILib;
import cn.zswltech.mithras.service.service.lib.LibAbstractHandler;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Set;

/**
 * 集团授信评审版本处理器
 *
 * @author wangchuanhao
 * @date 2022/7/19 10:49 PM
 */
public abstract class GroupCreditReviewLibAbstractHandler<LIB extends ILib, ENTITY extends IEntity, RSP extends ListBaseRSP> extends LibAbstractHandler<LIB, ENTITY, RSP> {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void flushData(String version, Long groupCreditReviewId, boolean needClearLast, Integer versionType) {
        if (!needHandle(groupCreditReviewId)) {
            return;
        }
        super.flushData(version, groupCreditReviewId, needClearLast, versionType);
    }

    /**
     * 审批拒绝 把版本表最新的数据还原到 临时表
     *
     * @param groupCreditReviewId
     * @param version
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reset(Long groupCreditReviewId, String version) {
        if (!needHandle(groupCreditReviewId)) {
            return;
        }
        super.reset(groupCreditReviewId, version);
    }

    public abstract GroupCreditReviewInfoModule getSubModule();

    public abstract boolean needHandle(Long mainId);

    @Override
    public String libMainIdFieldName() {
        return "group_credit_review_id";
    }

    @Override
    public String entityMainIdFieldName() {
        return "group_credit_review_id";
    }

    @Override
    public Set<String> compareIgnoreFieldNames() {
        return Collections.EMPTY_SET;
    }

    @Override
    public BusinessModuleEnum businessModuleEnum() {
        return BusinessModuleEnum.GROUP_CREDIT_REVIEW;
    }

}
