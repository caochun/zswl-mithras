package cn.zswltech.mithras.creditreport.enums;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;


/**
 * 担保方式代码表
 * @author: jackerhe
 * @date: 2025/11/17 14:43
 **/
@AllArgsConstructor
@Getter
public enum CreditReportGuaranteeMethodEnum implements PullDown {

    CREDIT(0, "信用/免担保"),
    GUARANTEE(1, "保证"),
    PLEDGE(2, "质押"),
    MORTGAGE(3, "抵押"),
    COMBINATION(4, "组合");

    private final int code;
    private final String display;

    @Override
    public String display() {
        return this.display;
    }

    public static CreditReportGuaranteeMethodEnum finaByName(String name) {
        for (CreditReportGuaranteeMethodEnum item : values()) {
            if (Objects.equals(item.name(), name)) {
                return item;
            }
        }
        return null;
    }


    public static CreditReportGuaranteeMethodEnum finaByCode(Integer code) {
        for (CreditReportGuaranteeMethodEnum item : values()) {
            if (Objects.equals(item.code, code)) {
                return item;
            }
        }
        return null;
    }

    public static CreditReportGuaranteeMethodEnum finaByDisplay(String display) {
        for (CreditReportGuaranteeMethodEnum item : values()) {
            if (Objects.equals(item.display(), display)) {
                return item;
            }
        }
        return null;
    }
}
