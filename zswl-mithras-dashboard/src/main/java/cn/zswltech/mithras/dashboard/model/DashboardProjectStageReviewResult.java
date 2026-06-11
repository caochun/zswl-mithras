package cn.zswltech.mithras.dashboard.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author dingqi
 * @date 2024/6/17
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DashboardProjectStageReviewResult extends DashboardProjectBasicResult {
    private String projReviewStatus;
    private String projReviewProcessStatus;
}
