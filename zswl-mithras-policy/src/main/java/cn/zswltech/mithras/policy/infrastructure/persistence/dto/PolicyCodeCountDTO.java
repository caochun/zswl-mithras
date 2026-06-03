package cn.zswltech.mithras.policy.infrastructure.persistence.dto;

import lombok.Data;

@Data
public class PolicyCodeCountDTO {


    /**
     * 保单编号
     */
    private String policyCode;

    private int policyNum;
}
