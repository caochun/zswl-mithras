package cn.zswltech.mithras.afterlease.application.auth;


import cn.zswltech.mithras.foundation.auth.DataAuthBusinessModule;
import cn.zswltech.mithras.foundation.auth.checker.AuthHelper;
import cn.zswltech.mithras.foundation.auth.checker.IDataAuthChecker;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author dingqi
 * @date 2022/11/22
 * @description
 */
@Component
public class AfterLeaseCheckPlanModifySubChecker implements IDataAuthChecker {
    @Resource
    private AuthHelper authHelper;
    @Resource
    private AfterLeaseCheckPlanModifyMainChecker modifyMainChecker;

    @Override
    public boolean check(DataAuthBusinessModule businessModule, Class<? extends BaseMapper> helperMapperClass, Long keyId, Object[] args) {
        Long mainId = authHelper.getMainIdFromSubTable(businessModule, helperMapperClass, keyId);
        modifyMainChecker.check(businessModule, businessModule.getMainMapperClass(), mainId, args);
        return true;
    }
}
