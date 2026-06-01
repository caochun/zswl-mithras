package cn.zswltech.mithras.service.service.bo;

import lombok.Data;

import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2022/11/18
 * @description
 */
@Data
public class AfterLeaseClientDataBO {
    /**
     * 客户id
     */
    private Long clientId;

    /**
     * 客户名称
     */
    private String clientName;

    /**
     * 行业
     */
    private String industry;

    /**
     * 合同总金额
     */
    private Long contractTotalAmount;

    /**
     * 风险敞口
     */
    private Long riskExposure;

    /**
     * 合同到期日
     */
    private LocalDate deadline;

    /**
     * 下次还款日
     */
    private LocalDate nextRepayDate;

    /**
     * 下次还款金额
     */
    private Long nextRepayAmount;
}
