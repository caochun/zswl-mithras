package cn.zswltech.mithras.afterlease.application.auth;


import cn.zswltech.mithras.foundation.auth.DataAuthBusinessModule;
import cn.zswltech.mithras.foundation.auth.checker.IDataAuthChecker;
import cn.zswltech.mithras.foundation.auth.DataAuthAssetManagerGuard;
import cn.zswltech.mithras.foundation.auth.DataAuthProcessGuard;
import cn.zswltech.mithras.afterlease.enums.AfterLeaseCheckPlanStatusEnum;
import cn.zswltech.mithras.afterlease.model.NewAfterLeaseCheckPlanBase;
import cn.zswltech.mithras.foundation.exception.AuthCheckException;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.context.SpringContextHolder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author dingqi
 * @date 2022/11/21
 * @description
 */
@Component
public class AfterLeaseCheckPlanModifyMainChecker implements IDataAuthChecker {
    @Resource
    private DataAuthAssetManagerGuard dataAuthAssetManagerRule;
    @Resource
    private DataAuthProcessGuard dataAuthProcessRule;

    @Override
    public boolean check(DataAuthBusinessModule businessModule, Class<? extends BaseMapper> helperMapperClass, Long keyId, Object[] args) {
        BaseMapper<?> baseMapper = SpringContextHolder.getApplicationContext().getBean(businessModule.getMainMapperClass());
        Object mainData = baseMapper.selectById(keyId);
        NewAfterLeaseCheckPlanBase planBase;
        if (mainData instanceof NewAfterLeaseCheckPlanBase) {
            planBase = (NewAfterLeaseCheckPlanBase) mainData;
        } else {
            throw new MithrasException("主表数据类型错误");
        }
        if (Objects.equals(planBase.getPlanStatus(), AfterLeaseCheckPlanStatusEnum.CLOSE.name())) {
            throw new AuthCheckException("检查计划已被关闭，不允许操作");
        }
        if (Objects.equals(planBase.getPlanStatus(), AfterLeaseCheckPlanStatusEnum.FINISH.name())) {
            throw new AuthCheckException("检查计划已完结，不允许操作");
        }
        dataAuthAssetManagerRule.check();
        dataAuthProcessRule.check(businessModule, keyId);
        return true;
    }
}
