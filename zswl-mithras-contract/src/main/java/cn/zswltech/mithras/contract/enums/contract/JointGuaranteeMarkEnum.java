package cn.zswltech.mithras.contract.enums.contract;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author dingqi
 * @date 2022/10/10
 * @description
 */
@AllArgsConstructor
@Getter
public enum JointGuaranteeMarkEnum implements PullDown {
    SINGLE("单人保证"),
    // 不再有多人分保。同一个担保合同编号下有多人的就联保，只有一人的就单人保证
    @Deprecated
    MULTIPLE_SEPARATE("多人分保"),
    JOINT("联保");

    private final String display;

    @Override
    public String display() {
        return this.display;
    }
}
