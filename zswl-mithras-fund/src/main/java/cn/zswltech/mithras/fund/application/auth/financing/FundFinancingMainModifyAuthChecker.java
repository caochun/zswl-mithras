package cn.zswltech.mithras.fund.application.auth.financing;


import cn.zswltech.mithras.foundation.auth.DataAuthBusinessModule;
import cn.zswltech.mithras.foundation.auth.checker.IDataAuthChecker;
import cn.zswltech.mithras.foundation.auth.DataAuthCreatorGuard;
import cn.zswltech.mithras.foundation.auth.DataAuthProcessGuard;
import cn.zswltech.mithras.fund.enums.financing.FundFinancingStatusEnum;
import cn.zswltech.mithras.fund.model.financing.FundFinancingBaseInfo;
import cn.zswltech.mithras.foundation.exception.AuthCheckException;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.context.SpringContextHolder;
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
    private DataAuthCreatorGuard dataAuthCreatorRule;
    @Resource
    private DataAuthProcessGuard dataAuthProcessRule;

    @Override
    public boolean check(DataAuthBusinessModule businessModule, Class<? extends BaseMapper> helperMapperClass, Long keyId, Object[] args) {
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
