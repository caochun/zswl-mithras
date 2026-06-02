package cn.zswltech.mithras.service.service.contract.effectcheck;

import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.others.MithrasException;

/**
 * @author dingqi
 * @date 2022/11/2
 * @description
 */
public interface ContractEffectCheck {
    void check(ContractBaseInfo contractBaseInfo, boolean inProcess) throws MithrasException;
}
