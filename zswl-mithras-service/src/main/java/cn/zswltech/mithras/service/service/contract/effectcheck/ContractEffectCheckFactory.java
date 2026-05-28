package cn.zswltech.mithras.service.service.contract.effectcheck;

import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.mithras.service.enums.common.ProjectBizType;

/**
 * @author dingqi
 * @date 2022/11/3
 * @description
 */
public class ContractEffectCheckFactory {
    public static ContractEffectCheck getInstance(String bizType) {
        if (ProjectBizType.BL.name().equals(bizType)) {
            return SpringUtil.getBean(BLContractEffectCheck.class);
        } else if (ProjectBizType.ZR.name().equals(bizType)) {
            return SpringUtil.getBean(ZRContractEffectCheck.class);
        } else {
            return SpringUtil.getBean(ZLContractEffectCheck.class);
        }
    }
}
