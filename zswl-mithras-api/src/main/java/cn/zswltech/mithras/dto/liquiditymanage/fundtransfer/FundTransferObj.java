package cn.zswltech.mithras.dto.liquiditymanage.fundtransfer;

import lombok.Data;

import java.time.LocalDate;


/**
 * AccountBalanceListREQ
 *
 * @author zhouning
 * @since 2024/12/24
 */
@Data
public class FundTransferObj {

    /**
     * id
     */
    private Long id;

    /**
     * 日期
     */
    private LocalDate date;

    /**
     * 待转资金余额
     */
    private Long pendingBalanceAmount;

    /**
     * 沉淀资金
     */
    private Long depositedAmount;

    /**
     * 沉淀时间
     */
    private Integer settingTime;

    /**
     * 待分配沉淀资金
     */
    private Long pendingDepositedAmount;

}
