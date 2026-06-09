package cn.zswltech.mithras.service.auth.checker.afterleasecheck;


import cn.zswltech.mithras.service.auth.DataAuthBusinessModule;
import cn.zswltech.mithras.service.auth.checker.IDataAuthChecker;
import cn.zswltech.mithras.service.auth.rule.DataAuthAssetManagerRule;
import cn.zswltech.mithras.service.auth.rule.DataAuthProcessRule;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.afterlease.domain.enums.AfterLeaseCheckPlanStatusEnum;
import cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.model.NewAfterLeaseCheckPlanBase;
import cn.zswltech.mithras.service.others.AuthCheckException;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.SpringContextHolder;
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
    private DataAuthAssetManagerRule dataAuthAssetManagerRule;
    @Resource
    private DataAuthProcessRule dataAuthProcessRule;

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
