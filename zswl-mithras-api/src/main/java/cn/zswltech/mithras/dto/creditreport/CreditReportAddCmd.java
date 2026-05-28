package cn.zswltech.mithras.dto.creditreport;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Data
@ApiModel("征信报告查询-新增-请求体")
public class CreditReportAddCmd {

    @ApiModelProperty("客户id")
    private Long clientId;

    @ApiModelProperty("客户名称")
    @NotBlank
    private String clientName;

    @ApiModelProperty("统一社会信用代码")
    @NotBlank
    private String cscCode;

    @ApiModelProperty("中征码")
    private String zhongZhengCode;

    @ApiModelProperty("关联项目id")
    private Long projId;

    @ApiModelProperty("关联项目id类型 PROJ_ESTABLISH-项目立项 GROUP_CREDIT_REVIEW-授信评审")
    private String projIdDataType;

    @ApiModelProperty("关联项目名称")
    private String projName;

    @ApiModelProperty("关联项目编号")
    private String projCode;

    @ApiModelProperty("查询版本")
    private String selectVersion;

    @ApiModelProperty("查询目的")
    @NotBlank
    private String selectGoal;

    @ApiModelProperty("信用报告封装格式")
    private String reportFormat;
    @ApiModelProperty("授信开始时间")
    private LocalDate authorizationBeganDate;
}
