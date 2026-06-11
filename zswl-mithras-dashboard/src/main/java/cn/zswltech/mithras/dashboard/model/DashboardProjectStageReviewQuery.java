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
public class DashboardProjectStageReviewQuery extends CommonAuthQuery {
    private Long clientId;
    private String projName;
    private String projectReviewStatus;
    private String projectReviewProcessStatus;
    private Long bizDeptId;
    private Long projSponsorUserId;
    // 1-评审阶段，2-评审通过未创建合同，3-评审提交待出具合规意见
    private Integer viewType;
    /**
     * "可见范围：全部项目，我的项目 - all ,own"
     */
    private String permissionType;
}
