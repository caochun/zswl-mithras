package cn.zswltech.mithras.contract.application.dto;

import lombok.Data;

/**
 * @author dingqi
 * @date 2023/7/24
 * @description
 */
@Data
public class ContractPrincipalBO {
    private Long contractId;
    private Long planPrincipal;
    private Long actualPrincipal;
}
