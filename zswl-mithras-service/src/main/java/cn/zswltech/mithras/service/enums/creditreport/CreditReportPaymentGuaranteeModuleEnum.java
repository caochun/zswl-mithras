package cn.zswltech.mithras.service.enums.creditreport;

import cn.hutool.core.collection.ListUtil;
import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Objects;

/**
 * 款项模块
 * @date 2023/2/20
 * @description
 */
@AllArgsConstructor
@Getter
public enum CreditReportPaymentGuaranteeModuleEnum implements PullDown {

    BANK_ACCEPTANCE_BILL(1, "银行承兑汇票"),
    LETTER_OF_CREDIT(2, "信用证"),
    BANK_GUARANTEE(3, "银行保函"),
    OTHER(9, "其他担保交易"),
    TOTAL(0,"合计");

    private final Integer code;
    private final String display;

    @Override
    public String display() {
        return this.display;
    }

    public static List<String> getGuarantee() {
        return ListUtil.toList(BANK_GUARANTEE.name(), OTHER.name());
    }


    public static CreditReportPaymentGuaranteeModuleEnum finaByDisplay(String display) {
        for (CreditReportPaymentGuaranteeModuleEnum item : values()) {
            if (Objects.equals(item.display(), display)) {
                return item;
            }
        }
        return null;
    }

    public static CreditReportPaymentGuaranteeModuleEnum finaByName(String name) {
        for (CreditReportPaymentGuaranteeModuleEnum item : values()) {
            if (Objects.equals(item.name(), name)) {
                return item;
            }
        }
        return null;
    }

    public static CreditReportPaymentGuaranteeModuleEnum finaByCode(Integer code) {
        for (CreditReportPaymentGuaranteeModuleEnum item : values()) {
            if (Objects.equals(item.code, code)) {
                return item;
            }
        }
        return null;
    }
}
