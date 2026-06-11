package cn.zswltech.mithras.foundation.enums;

import cn.zswltech.mithras.foundation.metadata.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * @author dingqi
 * @date 2022/8/19
 * @description
 */
@Getter
@AllArgsConstructor
public enum YesOrNoNumberEnum implements PullDown{
    YES(1, "是"),
    NO(0, "否");

    private final Integer code;
    private final String chinese;

    @Override
    public String display() {
        return String.valueOf(code);
    }

    public static YesOrNoNumberEnum findByChinese(String chinese) {
        for (YesOrNoNumberEnum item : values()) {
            if (Objects.equals(chinese, item.getChinese())) {
                return item;
            }
        }
        return null;
    }

    public static YesOrNoNumberEnum findByCodeStr(String code) {
        for (YesOrNoNumberEnum item : values()) {
            if (Objects.equals(code, item.getCode().toString())) {
                return item;
            }
        }
        return null;
    }
}
