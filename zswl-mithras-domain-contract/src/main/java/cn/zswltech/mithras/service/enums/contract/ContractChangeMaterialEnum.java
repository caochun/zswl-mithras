package cn.zswltech.mithras.service.enums.contract;

import cn.zswltech.mithras.service.config.enumscan.IMaterialsTypeConvert;
import cn.zswltech.mithras.service.config.enumscan.PullDown;
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
