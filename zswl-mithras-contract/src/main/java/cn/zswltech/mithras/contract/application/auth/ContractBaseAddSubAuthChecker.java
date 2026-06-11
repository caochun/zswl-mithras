package cn.zswltech.mithras.contract.application.auth;


import cn.hutool.core.lang.Assert;
import cn.zswltech.mithras.foundation.auth.DataAuthBusinessModule;
import cn.zswltech.mithras.foundation.auth.DataAuthProcessGuard;
import cn.zswltech.mithras.foundation.auth.DataAuthSponsorUserGuard;
import cn.zswltech.mithras.foundation.auth.checker.IDataAuthChecker;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.context.SpringContextHolder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Objects;

/**
 * @author jackerhe
 * @date 2022/8/31
 * @description
 */
@Component
public class ContractBaseAddSubAuthChecker implements IDataAuthChecker {

    @Resource
    private DataAuthSponsorUserGuard dataAuthSponsorUserGuard;
    @Resource
    private DataAuthProcessGuard dataAuthProcessGuard;

    @Override
    public boolean check(DataAuthBusinessModule businessModule, Class<? extends BaseMapper> helperMapperClass, Long keyId, Object[] args) {
        BaseMapper<?> baseMapper = SpringContextHolder.getApplicationContext().getBean(businessModule.getMainMapperClass());
        Object mainData = baseMapper.selectById(keyId);
        ContractBaseInfo contractBaseInfo;
        if (mainData instanceof ContractBaseInfo) {
            contractBaseInfo = (ContractBaseInfo) mainData;
        } else {
            throw new MithrasException("主表数据类型错误");
        }
        // 判断合同是否结清
        Assert.isTrue(!Objects.equals(ContractStatus.SETTLE.name(), contractBaseInfo.getContractStatus()), () -> MithrasException.newException("合同已结清，无法操作"));
        dataAuthSponsorUserGuard.check(businessModule, keyId);
        dataAuthProcessGuard.check(businessModule, keyId);
        return true;
    }

    @Override
    public boolean checkBatch(DataAuthBusinessModule businessModule, Class<? extends BaseMapper> helperMapperClass, Collection<Long> keyIds, Object[] args) {
        return false;
    }
}
