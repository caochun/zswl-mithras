package cn.zswltech.mithras.dto.dashboard;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author dingqi
 * @date 2024/6/18
 * @description
 */
@Data
public class DashboardProjectDeptUserBasicRSP {
    @ApiModelProperty("业务部门ID")
    private Long bizDeptId;
    @ApiModelProperty("业务部门名称")
    private String bizDeptName;
    @ApiModelProperty("项目主办ID")
    private Long projSponsorUserId;
    @ApiModelProperty("项目主办名称")
    private String projSponsorUserName;
    @ApiModelProperty("项目协办ID，多个用英文逗号分隔")
    private String projCosponsorUserIds;
    @ApiModelProperty("项目协办名称，多个用英文逗号分隔")
    private String projCosponsorUserNames;
}
