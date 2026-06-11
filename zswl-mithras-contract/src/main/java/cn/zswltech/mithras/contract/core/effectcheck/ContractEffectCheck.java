package cn.zswltech.mithras.contract.core.effectcheck;

import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.foundation.exception.MithrasException;

/**
 * @author dingqi
 * @date 2022/11/2
 * @description
 */
public interface ContractEffectCheck {
    void check(ContractBaseInfo contractBaseInfo, boolean inProcess) throws MithrasException;
}
