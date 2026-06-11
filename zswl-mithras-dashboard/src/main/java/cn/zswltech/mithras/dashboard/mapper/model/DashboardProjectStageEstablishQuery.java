package cn.zswltech.mithras.dashboard.mapper.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author dingqi
 * @date 2024/6/16
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DashboardProjectStageEstablishQuery extends CommonAuthQuery {
    private Long clientId;
    private String projName;
    private String projectEstablishStatus;
    private String projectEstablishProcessStatus;
    private Long bizDeptId;
    private Long projSponsorUserId;
    /**
     * "可见范围：全部项目，我的项目 - all ,own"
     */
    private String permissionType;
}
