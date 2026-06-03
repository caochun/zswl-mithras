package cn.zswltech.mithras.fund.domain.enums.financing;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.logging.log4j.util.Strings;

import java.util.Objects;

/**
 * @author dingqi
 * @date 2023/2/20
 * @description
 */
@AllArgsConstructor
@Getter
public enum FundFinancingBizTypeEnum implements PullDown {
    BANK_ACCEPTANCE("银行承兑汇票", "118"),
    LETTER_OF_CREDIT("信用证", "223"),
    FACTORING_FINANCING("保理融资", "130"),
    WORKING_CAPITAL_LOAN("流动资金贷款", "101"),
    PROJECT_LOAN("项目贷款", "102"),
    COMMERCE_ACCEPTANCE("商业承兑汇票", "118"),
    SYNDICATIONS("银团", "110"),
//    OTHER("其他")
//    ABS("ABS"),
    ;

    private final String display;
    /**
     * 财政公司系统编号
     */
    private final String financialSystemCode;

    @Override
    public String display() {
        return this.display;
    }

    public static FundFinancingBizTypeEnum finaByName(String name) {
        for (FundFinancingBizTypeEnum item : values()) {
            if (Objects.equals(item.name(), name)) {
                return item;
            }
        }
        return null;
    }

    public static String name2Display(String name){
        FundFinancingBizTypeEnum financingType = finaByName(name);
        if (financingType != null){
            return financingType.display;
        }
        return Strings.EMPTY;
    }
}
