package cn.zswltech.mithras.afterlease.application.auth;


import cn.zswltech.mithras.foundation.auth.DataAuthBusinessModule;
import cn.zswltech.mithras.foundation.auth.checker.IDataAuthChecker;
import cn.zswltech.mithras.foundation.auth.DataAuthAssetManagerGuard;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author dingqi
 * @date 2022/11/21
 * @description
 */
@Component
public class AfterLeaseCheckPlanAddMainChecker implements IDataAuthChecker {
    @Resource
    private DataAuthAssetManagerGuard dataAuthAssetManagerRule;

    @Override
    public boolean check(DataAuthBusinessModule businessModule, Class<? extends BaseMapper> helperMapperClass, Long keyId, Object[] args) {
        dataAuthAssetManagerRule.check();
        return true;
    }
}
