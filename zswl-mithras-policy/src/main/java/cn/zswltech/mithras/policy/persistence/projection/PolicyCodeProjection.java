package cn.zswltech.mithras.policy.persistence.projection;

import lombok.Data;

@Data
public class PolicyCodeProjection {

    /**
     * 保单编号
     */
    private String policyCode;

    private Integer codeNum;

}
