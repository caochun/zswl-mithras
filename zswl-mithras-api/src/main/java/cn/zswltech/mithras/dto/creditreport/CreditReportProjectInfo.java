package cn.zswltech.mithras.dto.creditreport;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@ApiModel("征信报告反显的项目信息")
@Accessors(chain = true)
public class CreditReportProjectInfo {

    @ApiModelProperty("项目id")
    private Long projId;

    @ApiModelProperty("项目id类型 PROJ_ESTABLISH-项目立项 GROUP_CREDIT_REVIEW-授信评审")
    private String projIdDataType;

    @ApiModelProperty("项目编码")
    private String projCode;

    @ApiModelProperty("项目名称")
    private String projName;
}
