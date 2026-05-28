package cn.zswltech.mithras.service.enums.basedata;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author dingqi
 * @date 2022/9/15
 * @description
 */
@AllArgsConstructor
@Getter
public enum BaseDataBankAccountTypeEnum implements PullDown {
    BASE("基本户", 10),
    NORMAL("一般户", 20),
    SUPERVISION("监管户", 30),
    OTHER("其他", 40)
    ;

    private final String display;
    private final Integer sort;

    public static BaseDataBankAccountTypeEnum find(String name) {
        for (BaseDataBankAccountTypeEnum item : values()) {
            if (item.name().equals(name)) {
                return item;
            }
        }
        return null;
    }

    @Override
    public String display() {
        return display;
    }
}
