package cn.zswltech.mithras.fund.directfinancing.application.auth;


import cn.zswltech.mithras.foundation.auth.DataAuthBusinessModule;
import cn.zswltech.mithras.foundation.auth.checker.IDataAuthChecker;
import cn.zswltech.mithras.foundation.auth.DataAuthCreatorGuard;
import cn.zswltech.mithras.fund.directfinancing.model.FundDirectFinancingBaseInfo;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.context.SpringContextHolder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author dingqi
 * @date 2023/2/24
 * @description
 */
@Component
public class FundDirectFinancingMainModifyAuthChecker implements IDataAuthChecker {
    @Resource
    private DataAuthCreatorGuard dataAuthCreatorRule;

    @Override
    public boolean check(DataAuthBusinessModule businessModule, Class<? extends BaseMapper> helperMapperClass, Long keyId, Object[] args) {
        BaseMapper<?> baseMapper = SpringContextHolder.getApplicationContext().getBean(businessModule.getMainMapperClass());
        Object mainData = baseMapper.selectById(keyId);
        if (mainData instanceof FundDirectFinancingBaseInfo) {

        } else {
            throw new MithrasException("主表数据类型错误");
        }
        dataAuthCreatorRule.check(businessModule, keyId);
        return true;
    }
}
