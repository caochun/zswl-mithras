package cn.zswltech.mithras.application.orchestration.contract.effectcheck;

import cn.hutool.core.lang.Assert;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import org.springframework.stereotype.Component;

/**
 * @author dingqi
 * @date 2022/11/2
 * @description
 */
@Component
public class BLContractEffectCheck extends AbstractContractEffectCheck {
    @Override
    public void check(ContractBaseInfo contractBaseInfo, boolean inProcess) throws MithrasException {
        Assert.notBlank(contractBaseInfo.getFactoringType(), () -> MithrasException.newException("保理类型不能为空"));
        this.creditDebtorCheck(contractBaseInfo);
        this.commonContractFileCheck(contractBaseInfo, inProcess);
    }
}
