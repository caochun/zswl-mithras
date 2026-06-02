package cn.zswltech.mithras.contract.enums.contract;

import cn.zswltech.mithras.service.config.enumscan.IMaterialsTypeConvert;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author dingqi
 * @date 2022/8/24
 * @description 补充协议类型
 */
@Getter
@AllArgsConstructor
public enum ContractExtraFileTypeEnum implements IMaterialsTypeConvert {
    // 合同结清补充协议
    CONTRACT_SETTLE("合同结清补充协议"),
    CONTRACT_SETTLE_OWN("所有权转移证书"),
    CONTRACT_SETTLE_OWN_SIGN("所有权转移证书（已用印）"),
    CHANGE("合同变更补充协议"),
    START_RENT("起租"),
    ;

    private final String display;

    @Override
    public String businessModule() {
        return "CONTRACT";
    }

    @Override
    public String display() {
        return display;
    }
}
