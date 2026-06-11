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
public class DashboardProjectStageContractQuery extends CommonAuthQuery {
    private Long clientId;
    private String projName;
    private String contractCode;
    private String contractProcessStatus;
    private Long bizDeptId;
    private Long projSponsorUserId;
    /**
     * "可见范围：全部项目，我的项目 - all ,own"
     */
    private String permissionType;
}
