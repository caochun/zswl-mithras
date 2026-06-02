package cn.zswltech.mithras.contract.overdue.domain.acl;

import lombok.Data;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/11/4 17:22
 */
@Data
public class ContractClientInfo {

    private Long clientId;
    private String name;
    /**
     * 合同id
     */
    private Long contractId;
    /**
     * 合同中的角色
    */
    private String role;
    /**
     * 证件类型
    */
    private String certificateType;
    /**
     * 证件号码
    */
    private String certificateNumber;
}
