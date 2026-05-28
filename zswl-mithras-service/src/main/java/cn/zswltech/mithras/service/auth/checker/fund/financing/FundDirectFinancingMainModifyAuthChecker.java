package cn.zswltech.mithras.service.auth.checker.fund.financing;

import cn.zswltech.mithras.service.auth.checker.IDataAuthChecker;
import cn.zswltech.mithras.service.auth.rule.DataAuthCreatorRule;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.fund.direct.entity.FundDirectFinancingBaseInfo;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.SpringContextHolder;
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
    private DataAuthCreatorRule dataAuthCreatorRule;

    @Override
    public boolean check(BusinessModuleEnum businessModule, Class<? extends BaseMapper> helperMapperClass, Long keyId, Object[] args) {
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
