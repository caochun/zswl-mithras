package cn.zswltech.mithras.application.orchestration.contract.effectcheck;

import cn.hutool.core.lang.Assert;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import org.springframework.stereotype.Component;

/**
 * @author dingqi
 * @date 2022/11/3
 * @description
 */
@Component
public class ZRContractEffectCheck extends AbstractContractEffectCheck {
    @Override
    public void check(ContractBaseInfo contractBaseInfo, boolean inProcess) throws MithrasException {
        Assert.notBlank(contractBaseInfo.getZrType(), () -> MithrasException.newException("转让类型不能为空"));
        this.creditDebtorCheck(contractBaseInfo);
        this.commonContractFileCheck(contractBaseInfo, inProcess);
    }
}
