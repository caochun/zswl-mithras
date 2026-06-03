package cn.zswltech.mithras.finance.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author dingqi
 * @date 2024/8/13
 * @description
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class FinanceProfitHelperBO {
    /**
     * 合同编号
     */
    private String contractCode;

    /**
     * 本年累计利息收入
     */
    private long interestIncomeThisYear;

    /**
     * 本年累计其他收入
     */
    private long otherIncomeThisYear;
}
