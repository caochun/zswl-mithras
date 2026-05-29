package cn.zswltech.mithras.service.service.lib.groupcreditestablish.handler;

import cn.zswltech.mithras.dto.ListBaseRSP;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.groupcreditestablish.GroupCreditEstablishInfoModule;
import cn.zswltech.mithras.common.model.IEntity;
import cn.zswltech.mithras.service.mapper.tag.ILib;
import cn.zswltech.mithras.service.service.lib.LibAbstractHandler;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Set;

/**
 * 集团授信立项版本处理器
 *
 * @author wangchuanhao
 * @date 2022/11/11 16:49 PM
 */
public abstract class GroupCreditEstablishLibAbstractHandler<LIB extends ILib, ENTITY extends IEntity, RSP extends ListBaseRSP> extends LibAbstractHandler<LIB, ENTITY, RSP> {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void flushData(String version, Long groupCreditEstablishId, boolean needClearLast, Integer versionType) {
        if (!needHandle(groupCreditEstablishId)) {
            return;
        }
        super.flushData(version, groupCreditEstablishId, needClearLast, versionType);
    }

    /**
     * 审批拒绝 把版本表最新的数据还原到 临时表
     *
     * @param groupCreditEstablishId
     * @param version
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reset(Long groupCreditEstablishId, String version) {
        if (!needHandle(groupCreditEstablishId)) {
            return;
        }
        super.reset(groupCreditEstablishId, version);
    }

    public abstract GroupCreditEstablishInfoModule getSubModule();

    public abstract boolean needHandle(Long mainId);

    @Override
    public String libMainIdFieldName() {
        return "group_credit_establish_id";
    }

    @Override
    public String entityMainIdFieldName() {
        return "group_credit_establish_id";
    }

    @Override
    public Set<String> compareIgnoreFieldNames() {
        return Collections.EMPTY_SET;
    }

    @Override
    public BusinessModuleEnum businessModuleEnum() {
        return BusinessModuleEnum.GROUP_CREDIT_ESTABLISH;
    }

}
