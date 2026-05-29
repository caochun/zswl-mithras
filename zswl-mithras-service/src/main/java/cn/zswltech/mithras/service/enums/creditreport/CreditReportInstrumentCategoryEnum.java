package cn.zswltech.mithras.service.enums.creditreport;

import cn.zswltech.mithras.common.enums.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;


/**
 * 企业担保交易业务类型代码表
 * @author: jackerhe
 * @date: 2025/11/17 14:43
 **/
@AllArgsConstructor
@Getter
public enum CreditReportInstrumentCategoryEnum implements PullDown {

    BILL("票据类"),
    CREDIT("信用工具"),
    GUARANTEE("担保类"),
    OTHERS("其他类");

    private final String display;

    @Override
    public String display() {
        return this.display;
    }

    public static CreditReportInstrumentCategoryEnum finaByName(String name) {
        for (CreditReportInstrumentCategoryEnum item : values()) {
            if (Objects.equals(item.name(), name)) {
                return item;
            }
        }
        return null;
    }

    public static CreditReportInstrumentCategoryEnum finaByDisplay(String display) {
        for (CreditReportInstrumentCategoryEnum item : values()) {
            if (Objects.equals(item.display(), display)) {
                return item;
            }
        }
        return null;
    }
}
