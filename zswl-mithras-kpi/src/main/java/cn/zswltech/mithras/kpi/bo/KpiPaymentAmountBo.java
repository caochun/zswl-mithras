package cn.zswltech.mithras.kpi.bo;

import lombok.Data;

import java.time.LocalDate;

@Data
public class KpiPaymentAmountBo {

    private Long id;

    /**
     * 生效月份
     */
    private LocalDate effectMonth;

    /**
     * 批次号
     */
    private Integer batchNumber;

    /**
     * 合同id
     */
    private Long contractId;

    /**
     * 当月投放金额
     */
    private Long paymentAmount;

}