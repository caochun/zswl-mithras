package cn.zswltech.mithras.contract.enums.contract;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author dingqi
 * @date 2023/3/7
 * @description 抵押物类型
 */
@AllArgsConstructor
@Getter
public enum MortgageItemTypeEnum implements PullDown {
    LEASE_ITEM("租赁物抵押"),
    SUBSTANTIVE_ITEM("实质性抵押");

    private final String display;

    @Override
    public String display() {
        return this.display;
    }
}
