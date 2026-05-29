package cn.zswltech.mithras.report.enums.biz;

import cn.zswltech.mithras.common.enums.PullDown;
import cn.zswltech.mithras.service.config.enumscan.PullDownExt;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * 评估机构类型
 *
 * @author wangchuanhao
 * @date 2022/10/10 10:36 AM
 */
@AllArgsConstructor
@Getter
@PullDownExt("crMortgageAppraisalCompanyType")
public enum MortgageAppraisalCompanyTypeEnum implements PullDown {

    SELF("1", "自评估"),
    THIRD("2", "第三方评估机构"),

    ;

    private String value;
    private String display;

    public static String convert(Integer originCode) {
        if (Objects.isNull(originCode)) {
            return null;
        }
        switch (originCode) {
            case 0:
                return SELF.value;
            case 1:
                return THIRD.value;
        }
        return null;
    }

    public static MortgageAppraisalCompanyTypeEnum getByValue(String value) {
        for (MortgageAppraisalCompanyTypeEnum valueEnum : values()) {
            if (valueEnum.value.equals(value)) {
                return valueEnum;
            }
        }
        return null;
    }

    @Override
    public String display() {
        return display;
    }

    @Override
    public String valueKey() {
        return value;
    }
}
