package cn.zswltech.mithras.service.mapper.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/15 17:32
 */
@Data
public class PaymentListDto extends BaseAuthDTO {

    private Long clientId;
    private Long contractId;
    private String contractCode;
    private String writeOffStatus;
    private String paymentProcessStatus;
    private Long applyPaymentAmountFrom;
    private Long applyPaymentAmountTo;
    private LocalDateTime applyPaymentDateFrom;
    private LocalDateTime applyPaymentDateTo;
    private LocalDateTime paidInDateFrom;
    private LocalDateTime paidInDateTo;
    private Long bizDeptId;


    private String paymentStatus;
    private List<String> paymentStatusList;
    /**
     * 注意是数据库字段名
     */
    private String orderFieldName;
    /**
     * asc/ desc
     */
    private String order;
}
