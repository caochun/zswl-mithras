package cn.zswltech.mithras.dashboard.mapper.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author dingqi
 * @date 2024/6/17
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DashboardProjectStageContractResult extends DashboardProjectBasicResult {
    private String contractCode;
    private String contractStatus;
    private String contractProcessStatus;
    private Long contractAmount;
    private Integer lprPercent;
    private Integer lprAddPercent;
    private Integer leaseDuration;
}
