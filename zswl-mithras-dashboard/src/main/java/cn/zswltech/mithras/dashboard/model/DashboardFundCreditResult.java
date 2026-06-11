package cn.zswltech.mithras.dashboard.model;

import lombok.Data;

import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2024/6/27
 * @description
 */
@Data
public class DashboardFundCreditResult {
    private Long id;
    private String creditCode;
    private String organizationName;
    private String businessType;
    private Long totalCreditLimit;
    private Integer recyclable;
    private LocalDate deadline;
}
