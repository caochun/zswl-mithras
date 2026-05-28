package cn.zswltech.mithras.report.enums.biz;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author yangxiong
 * @date 2024/1/12/14:18
 * @description 资信法人、自然人对应枚举
 */
@Getter
@AllArgsConstructor
public enum ReportClientTypeEnum {
    /*法人*/
    CORPORATION("20", "法人"),
    /*自然人*/
    NORMAL("10", "自然人");

    private final String code;
    private final String value;

    public static ReportClientTypeEnum getByValue(String value) {
        for (ReportClientTypeEnum reportClientTypeEnum : ReportClientTypeEnum.values()) {
            if (reportClientTypeEnum.value.equals(value)) {
                return reportClientTypeEnum;
            }
        }
        return null;
    }
}
