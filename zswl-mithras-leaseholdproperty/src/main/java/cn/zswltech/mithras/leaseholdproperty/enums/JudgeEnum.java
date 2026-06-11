package cn.zswltech.mithras.leaseholdproperty.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @ClassName JudgeEnum
 * @Description
 * @Author jackerhe
 * @Date 2022/7/27 11:02 上午
 * @Version 1.0
 **/
public enum JudgeEnum {

    TRUE("1"),//是
    FALSE("0");  //否


    private final String type;

    JudgeEnum(String type) {
        this.type = type;
    }

    @JsonValue
    public String getType() {
        return type;
    }

    public static JudgeEnum of(String type) {
        for (JudgeEnum value : JudgeEnum.values()) {
            if (value.getType().equals(type)) {
                return value;
            }
        }
        return null;
    }

    public static JudgeEnum ofByName(String name) {
        for (JudgeEnum value : JudgeEnum.values()) {
            if (value.name().equals(name)) {
                return value;
            }
        }
        return null;
    }
}
