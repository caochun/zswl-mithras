package cn.zswltech.mithras.contract.enums.contract;

import cn.zswltech.mithras.foundation.metadata.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * 抵押类型
 *
 * @author yupengfei
 * @date 2024/6/18 11:12
 */
@AllArgsConstructor
@Getter
public enum MortgageTypeEnum implements PullDown {

    CHATTEL_MORTGAGE("动产抵押"),
    REAL_ESTATE_MORTGAGE("不动产抵押"),
    OTHER_MORTGAGE("其他抵押"),
    ;

    private final String display;

    @Override
    public String display() {
        return this.display;
    }

    public static MortgageTypeEnum findByName(String name) {
        for (MortgageTypeEnum item : values()) {
            if (Objects.equals(item.name(), name)) {
                return item;
            }
        }
        return null;
    }
}
