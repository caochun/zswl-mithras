package cn.zswltech.mithras.report.mapper.query;

import lombok.Data;

/**
 * 还款表查询
 *
 * @author wangchuanhao
 * @date 2023/1/13 11:32 AM
 */
@Data
public class CrRepayQuery {

    /**
     * 付款申请编号
     */
    private String paymentApplyCode;

    /**
     * 是否上报枚举
     */
    private String reportState;

    /**
     * 为 1 时过滤account上报逻辑
     */
    private Integer filterAccountReportFlag;

    /**
     * 审批状态
     */
    private String approvalStatus;

    /**
     * 批次号
     */
    private Long batchId;

    /**
     * 付款申请id
     */
    private Long paymentId;

    /**
     * 流程businessKey
     */
    private String procBusinessKey;
    
}
