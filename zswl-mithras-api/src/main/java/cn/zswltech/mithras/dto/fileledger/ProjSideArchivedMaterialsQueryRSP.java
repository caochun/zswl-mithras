package cn.zswltech.mithras.dto.fileledger;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 项目端归档资料
 *
 * @author gxy
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjSideArchivedMaterialsQueryRSP {

    @ApiModelProperty("业务部门名")
    private String bizDeptName;

    @ApiModelProperty("客户名称")
    private String clientName;

    @ApiModelProperty("项目名称")
    private String projName;

    @ApiModelProperty("项目编号")
    private String projCode;

    @ApiModelProperty("合同编号")
    private String contractCode;

    @ApiModelProperty("风控行业分类")
    private String riskControlIndustryClassify;

    @ApiModelProperty("项目主办")
    private String projSponsorUserName;

    @ApiModelProperty("档案复核人")
    private String materialsReReviewUserName;

    @ApiModelProperty("流程ID")
    private String processInstanceId;

    @ApiModelProperty("归档超期标识")
    private String archiveOverDueFlag;

    @ApiModelProperty("补充材料超期标识")
    private String supplementDocOverdueFlag;

    @ApiModelProperty("全流程耗时（工作日）")
    private Long duration;

    @ApiModelProperty("档案管理初审耗时（工作日）")
    private Long materialsManagerReviewDuration;

    @ApiModelProperty("档案管理复核耗时（工作日）")
    private Long materialsManagerReReviewDuration;

    @ApiModelProperty(value = "发起时间")
    private LocalDateTime createTime;

    @ApiModelProperty("结束时间")
    private LocalDateTime endTime;

    @ApiModelProperty("应归档日")
    private LocalDate dueArchiveDate;

    @ApiModelProperty("项目主办提交时间")
    private LocalDateTime projectHostSubmitTime;

    @ApiModelProperty("档案管理初审提交时间")
    private LocalDateTime archiveReviewSubmitTime;

    @ApiModelProperty("档案管理复核提交时间")
    private LocalDateTime archiveReReviewSubmitTime;

    @ApiModelProperty("档案管理初审退回次数")
    private Integer archiveReviewRejectCount;

    @ApiModelProperty("档案管理复核退回次数")
    private Integer archiveReReviewRejectCount;

    @ApiModelProperty("档案管理初审退回原因")
    private String archiveReviewRejectReason;

    @ApiModelProperty("档案管理复核退回原因")
    private String archiveReReviewRejectReason;
}
