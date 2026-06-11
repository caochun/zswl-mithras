package cn.zswltech.mithras.fund.enums.financial;

import cn.zswltech.mithras.foundation.metadata.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;


/**
 * @author zswl
 */
@Getter
@AllArgsConstructor
public enum FundFinancialSystemEnum implements PullDown {

    REPAYMENT("贷款还款计划","20001001","20001"),
    CONTRACT("贷款合同推送","20002001","20002"),
    RECEIPT("贷款借据推送","20003001","20003"),
    ;

    private String display;
    private String serviceNo;
    private String consumerCode;



    @Override
    public String display() {
        return display;
    }
}
