package cn.zswltech.mithras.dto.creditreport;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@ApiModel("征信报告查询列表-请求参数")
public class CreditReportListREQ extends PageReq {

    private List<Long> creditReportBaseInfoIds;

    @ApiModelProperty("查询编号")
    private String creditCode;

    @ApiModelProperty("客户名称")
    private String clientName;

    @ApiModelProperty("统一社会信用代码")
    private String cscCode;

    @ApiModelProperty("申请人")
    private Long applyUser;

    @ApiModelProperty("申请部门")
    private Long applyOrg;

    @ApiModelProperty("查询完成时间-开始")
    private LocalDate createFrom;

    @ApiModelProperty("查询完成时间-结束")
    private LocalDate createTo;

    @ApiModelProperty("项目名称")
    private String projName;

    @ApiModelProperty("项目编号")
    private String projCode;

    @ApiModelProperty("查询目的")
    private String selectGoal;

    @ApiModelProperty("项目id")
    private Long projId;

    @ApiModelProperty("客户id")
    private Long clientId;

    private Set<Long> targetClientIds;

    private LocalDateTime searchTimeFrom;

    private LocalDateTime searchTimeTo;

}
