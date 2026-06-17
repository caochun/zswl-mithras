package cn.zswltech.mithras.afterlease.application.auth;


import cn.zswltech.mithras.afterlease.application.AfterLeaseWorkflowPort;
import cn.zswltech.mithras.foundation.auth.DataAuthBusinessModule;
import cn.zswltech.mithras.foundation.auth.checker.IDataAuthChecker;
import cn.zswltech.mithras.foundation.auth.DataAuthSponsorUserGuard;
import cn.zswltech.mithras.foundation.exception.AuthCheckException;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

@Component
public class AfterLeaseCheckReportModifyMainChecker implements IDataAuthChecker {
    @Resource
    private DataAuthSponsorUserGuard dataAuthSponsorUserRule;
    @Resource
    private AfterLeaseWorkflowPort afterLeaseWorkflowPort;

    @Override
    public boolean check(DataAuthBusinessModule businessModule, Class<? extends BaseMapper> helperMapperClass, Long keyId, Object[] args) {
        if (Objects.isNull(keyId)) {
            throw new AuthCheckException("id不能为空");
        }
        dataAuthSponsorUserRule.check(businessModule, keyId);
        if (!afterLeaseWorkflowPort.canModifyAtCurrentProcessNode(keyId, businessModule.getModelKeyList())) {
            throw new AuthCheckException("该数据处于流程中，且流程不在发起人/项目经理节点，不允许修改数据");
        }
        return true;
    }
}
