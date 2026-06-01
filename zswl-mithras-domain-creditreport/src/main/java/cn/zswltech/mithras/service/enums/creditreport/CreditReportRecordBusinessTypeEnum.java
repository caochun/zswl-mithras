package cn.zswltech.mithras.service.enums.creditreport;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
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
public enum CreditReportRecordBusinessTypeEnum implements PullDown {

    ENTERPRISE_BOND(10, "企业债"),
    LOAN(11, "贷款"),
    TRADE_FINANCING(12, "贸易融资"),
    FACTORING(13, "保理融资"),
    FINANCIAL_LEASING(14, "融资租赁"),
    SECURITIES_FINANCING(15, "证券类融资"),
    OVERDRAFT(16, "透支"),
    BILL_DISCOUNT(21, "票据贴现"),
    GOLD_LOAN(31, "黄金借贷"),
    ADVANCE_PAYMENT(41, "垫款"),
    ASSET_DISPOSAL(51, "资产处置");

    private final Integer code;
    private final String display;

    @Override
    public String display() {
        return this.display;
    }

    public static CreditReportRecordBusinessTypeEnum finaByName(String name) {
        for (CreditReportRecordBusinessTypeEnum item : values()) {
            if (Objects.equals(item.name(), name)) {
                return item;
            }
        }
        return null;
    }


    public static CreditReportRecordBusinessTypeEnum finaByCode(Integer code) {
        for (CreditReportRecordBusinessTypeEnum item : values()) {
            if (Objects.equals(item.code, code)) {
                return item;
            }
        }
        return null;
    }

    public static CreditReportRecordBusinessTypeEnum finaByDisplay(String display) {
        for (CreditReportRecordBusinessTypeEnum item : values()) {
            if (Objects.equals(item.display(), display)) {
                return item;
            }
        }
        return null;
    }
}
