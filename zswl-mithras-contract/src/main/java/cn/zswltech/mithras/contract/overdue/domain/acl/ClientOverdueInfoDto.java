package cn.zswltech.mithras.contract.overdue.domain.acl;

import lombok.Data;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/11/7 14:26
 */
@Data
public class ClientOverdueInfoDto {
    private Long clientId;
    private String clientName;
    private Integer overdueDays;
    private Long overdueAmount;
    private Long riskExposure;
}
