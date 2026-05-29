package cn.zswltech.mithras.service.enums.creditreport;

import cn.zswltech.mithras.common.enums.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;


/**
 * 相关还款责任类型代码表
 * @author: jackerhe
 * @date: 2025/11/17 14:43
 **/
@AllArgsConstructor
@Getter
public enum CreditReportRepaymentLiabilityEnum implements PullDown {

    PERSONAL_CO_BORROWER("个人信贷交易共同还款人/共同债务人", 1),
    GUARANTOR_COUNTER_GUARANTOR("保证人/反担保人", 2),
    BILL_ACCEPTOR("票据承兑人", 3),
    AR_DEBTOR("应收账款债务人", 4),
    SUPPLY_CHAIN_CORE("供应链中核心企业", 5),
    OTHER("其他", 9),
    TOTAL("合计", 0);
    private final String display;
    private final Integer code;

    @Override
    public String display() {
        return this.display;
    }

    public static CreditReportRepaymentLiabilityEnum finaByName(String name) {
        for (CreditReportRepaymentLiabilityEnum item : values()) {
            if (Objects.equals(item.name(), name)) {
                return item;
            }
        }
        return null;
    }


    public static CreditReportRepaymentLiabilityEnum finaByCode(Integer code) {
        for (CreditReportRepaymentLiabilityEnum item : values()) {
            if (Objects.equals(item.code, code)) {
                return item;
            }
        }
        return null;
    }

    public static CreditReportRepaymentLiabilityEnum finaByDisplay(String display) {
        for (CreditReportRepaymentLiabilityEnum item : values()) {
            if (Objects.equals(item.display(), display)) {
                return item;
            }
        }
        return null;
    }
}
