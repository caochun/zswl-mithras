package cn.zswltech.mithras.service.service.bo;

import lombok.Data;

import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2024/4/9
 * @description 收入分摊现金流
 */
@Data
public class IncomeSharingCashFlowBO {
    /**
     * 日期
     */
    private LocalDate cashFlowDate;
    /**
     * 期项
     */
    private Integer cashFlowPhase;
    /**
     * 长期应收款期初余额（毫厘）
     */
    private Long beginOfTermBalance;
    /**
     * 当天应收租金（毫厘）
     */
    private Long rent;
    /**
     * 当天确认收入（毫厘）
     */
    private Long income;
    /**
     * 长期应收款期末余额（毫厘）
     */
    private Long endOfTermBalance;
}
