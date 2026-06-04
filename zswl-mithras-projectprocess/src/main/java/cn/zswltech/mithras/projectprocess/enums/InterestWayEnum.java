package cn.zswltech.mithras.projectprocess.enums;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author dingqi
 * @date 2023/11/22
 * @description
 */
@AllArgsConstructor
@Getter
public enum InterestWayEnum implements PullDown {
    ACTUAL_RATE("实际利率法"),
    FLAT_RATE("平息法");

    private final String display;

    @Override
    public String display() {
        return this.display;
    }
}
