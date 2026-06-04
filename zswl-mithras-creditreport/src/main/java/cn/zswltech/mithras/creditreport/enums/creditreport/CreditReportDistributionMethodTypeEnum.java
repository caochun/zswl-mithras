package cn.zswltech.mithras.creditreport.enums;

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
public enum CreditReportDistributionMethodTypeEnum implements PullDown {

    NEW_ADDITION(1, "新增"),
    RECOVERY_AND_RELOAN(2, "收回再贷"),
    BORROW_NEW_TO_REPAY_OLD(3, "借新还旧"),
    ASSET_RESTRUCTURING(4, "资产重组"),
    TRANSFER_FROM_OTHER_INSTITUTION(5, "其他机构转入"), // 注意：原数据中 5 重复，这里保留一个
    OTHER(9, "其他");

    private final int code;
    private final String display;

    @Override
    public String display() {
        return this.display;
    }

    public static CreditReportDistributionMethodTypeEnum finaByName(String name) {
        for (CreditReportDistributionMethodTypeEnum item : values()) {
            if (Objects.equals(item.name(), name)) {
                return item;
            }
        }
        return null;
    }


    public static CreditReportDistributionMethodTypeEnum finaByCode(Integer code) {
        for (CreditReportDistributionMethodTypeEnum item : values()) {
            if (Objects.equals(item.code, code)) {
                return item;
            }
        }
        return null;
    }

    public static CreditReportDistributionMethodTypeEnum finaByDisplay(String display) {
        for (CreditReportDistributionMethodTypeEnum item : values()) {
            if (Objects.equals(item.display(), display)) {
                return item;
            }
        }
        return null;
    }
}
