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
public enum CreditReportLastRepaymentTypeEnum implements PullDown {

    NORMAL_REPAYMENT(10, "正常还款"),
    GUARANTEE_COMPENSATION(21, "担保代偿"),
    POLICY_REPAYMENT(22, "政策性还款"),
    ASSET_RESTRUCTURING(31, "资产重组"),
    REMISSION(32, "减免"),
    CREDIT_ASSIGNMENT(41, "债权转让"),
    CREDIT_REPLACEMENT(42, "债权置换"),
    ASSET_DIVESTMENT(43, "资产剥离"),
    DEBT_TO_EQUITY_SWAP(51, "债转股"),
    ASSET_IN_LIEU_OF_DEBT(52, "以资抵债"),
    LITIGATION_RECOVERY(61, "诉讼追偿"),
    BANKRUPTCY_LIQUIDATION(62, "破产清偿"),
    ENTRUSTED_DISPOSAL(63, "委托处置"),
    OTHER(99, "其他"),;

    private final int code;
    private final String display;

    @Override
    public String display() {
        return this.display;
    }

    public static CreditReportLastRepaymentTypeEnum finaByName(String name) {
        for (CreditReportLastRepaymentTypeEnum item : values()) {
            if (Objects.equals(item.name(), name)) {
                return item;
            }
        }
        return null;
    }


    public static CreditReportLastRepaymentTypeEnum finaByCode(Integer code) {
        for (CreditReportLastRepaymentTypeEnum item : values()) {
            if (Objects.equals(item.code, code)) {
                return item;
            }
        }
        return null;
    }

    public static CreditReportLastRepaymentTypeEnum finaByDisplay(String display) {
        for (CreditReportLastRepaymentTypeEnum item : values()) {
            if (Objects.equals(item.display(), display)) {
                return item;
            }
        }
        return null;
    }
}
