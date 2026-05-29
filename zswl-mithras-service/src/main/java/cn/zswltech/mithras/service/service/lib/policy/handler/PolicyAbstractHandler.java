package cn.zswltech.mithras.service.service.lib.policy.handler;

import cn.zswltech.mithras.dto.ListBaseRSP;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.mapper.model.client.Client;
import cn.zswltech.mithras.common.model.IEntity;
import cn.zswltech.mithras.service.mapper.tag.ILib;
import cn.zswltech.mithras.service.service.lib.LibAbstractHandler;
import cn.zswltech.mithras.service.service.lib.payment.handler.PaymentInfoModule;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Set;


public abstract class PolicyAbstractHandler<LIB extends ILib, ENTITY extends IEntity, RSP extends ListBaseRSP>
        extends LibAbstractHandler<LIB, ENTITY, RSP> {

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void flushData(String version, Long policyId, boolean needClearLast, Integer versionType) {
        if (!needHandle(policyId)) {
            return;
        }
        super.flushData(version, policyId, needClearLast, versionType);
    }

    /**
     * 审批拒绝 把版本表最新的数据还原到 临时表
     *
     * @param policyId
     * @param version
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void reset(Long policyId, String version) {
        if (!needHandle(policyId)) {
            return;
        }
        super.reset(policyId, version);
    }

    @Override
    public String libMainIdFieldName() {
        return "policy_id";
    }

    @Override
    public String entityMainIdFieldName() {
        return "policy_id";
    }

    public void validateData(Client client) {
    }

    public abstract PolicyInfoModule getSubModule();

    public abstract boolean needHandle(Long mainId);

    @Override
    public Set<String> compareIgnoreFieldNames() {
        return Collections.EMPTY_SET;
    }

    @Override
    public BusinessModuleEnum businessModuleEnum() {
        return BusinessModuleEnum.POLICY;
    }

}
