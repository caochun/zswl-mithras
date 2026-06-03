package cn.zswltech.mithras.margin.enums;

import cn.zswltech.mithras.service.config.enumscan.PullDown;

/**
 * @create: 2022-08-18
 **/
public enum MarginWriteOffStatusEnum implements PullDown {
    TO_BE_WRITE_OFF("待核销"),
    TO_BE_REVIEW("待复核"),
    REVIEWED("已复核"),
    WRITTEN_OFF("已核销"),
    WRITE_OFF_COMPLETED("核销完毕"),
    IGNORE("忽略");
    public final String display;
    MarginWriteOffStatusEnum(String display){
        this.display = display;
    }
    public static MarginWriteOffStatusEnum of(String name) {
        for (MarginWriteOffStatusEnum value : MarginWriteOffStatusEnum.values()) {
            if (value.name().equals(name)) {
                return value;
            }
        }
        return null;
    }
    @Override
    public String display() {
        return display;
    }
}
