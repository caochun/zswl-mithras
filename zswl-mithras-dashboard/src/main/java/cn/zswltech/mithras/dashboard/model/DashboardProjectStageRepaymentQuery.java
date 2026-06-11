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
public class DashboardProjectStageRepaymentQuery extends CommonAuthQuery {
    private String contractCode;
    private String projName;
    private Long rentBalanceFrom;
    private Long rentBalanceTo;
    private Long clientId;
    /**
     * "可见范围：全部项目，我的项目 - all ,own"
     */
    private String permissionType;
}
