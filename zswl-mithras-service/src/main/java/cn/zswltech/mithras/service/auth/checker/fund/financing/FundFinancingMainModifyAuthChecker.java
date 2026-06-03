package cn.zswltech.mithras.service.auth.checker.fund.financing;

import cn.zswltech.mithras.service.auth.checker.IDataAuthChecker;
import cn.zswltech.mithras.service.auth.rule.DataAuthCreatorRule;
import cn.zswltech.mithras.service.auth.rule.DataAuthProcessRule;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.fund.domain.enums.financing.FundFinancingStatusEnum;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.financing.FundFinancingBaseInfo;
import cn.zswltech.mithras.service.others.AuthCheckException;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author dingqi
 * @date 2023/2/24
 * @description
 */
@Component
public class FundFinancingMainModifyAuthChecker implements IDataAuthChecker {
    @Resource
    private DataAuthCreatorRule dataAuthCreatorRule;
    @Resource
    private DataAuthProcessRule dataAuthProcessRule;

    @Override
    public boolean check(BusinessModuleEnum businessModule, Class<? extends BaseMapper> helperMapperClass, Long keyId, Object[] args) {
        BaseMapper<?> baseMapper = SpringContextHolder.getApplicationContext().getBean(businessModule.getMainMapperClass());
        Object mainData = baseMapper.selectById(keyId);
        FundFinancingBaseInfo financingBaseInfo;
        if (mainData instanceof FundFinancingBaseInfo) {
            financingBaseInfo = (FundFinancingBaseInfo) mainData;
        } else {
            throw new MithrasException("主表数据类型错误");
        }
        if (Objects.equals(financingBaseInfo.getFinancingStatus(), FundFinancingStatusEnum.CLOSE.name())) {
            throw new AuthCheckException("融资已关闭，不允许操作");
        }
        if (Objects.equals(financingBaseInfo.getFinancingStatus(), FundFinancingStatusEnum.SETTLE.name())) {
            throw new AuthCheckException("融资已结清，不允许操作");
        }
        dataAuthCreatorRule.check(businessModule, keyId);
        dataAuthProcessRule.check(businessModule, keyId);
        return true;
    }
}
