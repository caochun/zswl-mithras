package cn.zswltech.mithras.service.auth.checker.afterleasecheck;

import cn.zswltech.mithras.service.auth.checker.IDataAuthChecker;
import cn.zswltech.mithras.service.auth.rule.DataAuthAssetManagerRule;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
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
    private DataAuthAssetManagerRule dataAuthAssetManagerRule;

    @Override
    public boolean check(BusinessModuleEnum businessModule, Class<? extends BaseMapper> helperMapperClass, Long keyId, Object[] args) {
        dataAuthAssetManagerRule.check();
        return true;
    }
}
