package cn.zswltech.mithras.payment.dto;

import lombok.Data;

/**
 * @author dingqi
 * @date 2025/6/16
 * @description
 */
@Data
public class ContractPayInfoDTO {
    private Long contractId;
    private Long belongDeptId;
    private Long payAmount;
    private Long firstRentAmount;
    private Long principalAmount;
}
