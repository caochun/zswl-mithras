package cn.zswltech.mithras.contract.enums.contract;

import cn.zswltech.mithras.foundation.metadata.IMaterialsTypeConvert;
import cn.zswltech.mithras.foundation.metadata.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author dingqi
 * @date 2023/1/3
 * @description
 */
@AllArgsConstructor
@Getter
public enum ContractChangeMaterialEnum implements PullDown, IMaterialsTypeConvert {
    EXCHANGE_MATERIAL("变更材料");

    private final String display;

    @Override
    public String businessModule() {
        return "CONTRACT";
    }

    @Override
    public String display() {
        return this.display;
    }
}
