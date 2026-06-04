package cn.zswltech.mithras.creditreport.enums;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * 款项模块
 * @date 2023/2/20
 * @description
 */
@AllArgsConstructor
@Getter
public enum CreditReportPaymentModuleEnum implements PullDown {

    MEDIUM_AND_LONG_TERM_LOAN("中长期借款", 1),
    SHORT_TERM_LOAN("短期借款", 2),
    OVERDRAFT("循环透支", 3),
    DISCOUNT("贴现", 4),
    TOTAL("合计", 0);

    private final String display;
    private final Integer code;

    @Override
    public String display() {
        return this.display;
    }


    public static CreditReportPaymentModuleEnum finaByDisplay(String display) {
        for (CreditReportPaymentModuleEnum item : values()) {
            if (Objects.equals(item.display(), display)) {
                return item;
            }
        }
        return null;
    }


    public static CreditReportPaymentModuleEnum finaByName(String name) {
        for (CreditReportPaymentModuleEnum item : values()) {
            if (Objects.equals(item.name(), name)) {
                return item;
            }
        }
        return null;
    }

    public static CreditReportPaymentModuleEnum finaByCode(Integer code) {
        for (CreditReportPaymentModuleEnum item : values()) {
            if (Objects.equals(item.code, code)) {
                return item;
            }
        }
        return null;
    }
}
