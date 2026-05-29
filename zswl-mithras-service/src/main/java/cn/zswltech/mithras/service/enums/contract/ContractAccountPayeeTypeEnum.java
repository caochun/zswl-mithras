package cn.zswltech.mithras.service.enums.contract;

import cn.zswltech.mithras.common.enums.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author dingqi
 * @date 2023/3/13
 * @description
 */
@Getter
@AllArgsConstructor
public enum ContractAccountPayeeTypeEnum implements PullDown {
    JIA("甲方"),
    YI("乙方"),
    SELLER("卖方"),
    OTHER("其它");

    private final String display;

    @Override
    public String display() {
        return this.display;
    }
}
