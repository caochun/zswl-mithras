package cn.zswltech.mithras.dto.afterlease;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/11/18 10:03
 */
@ApiModel("外部查询详情")
@Data
public class AfterLeaseCheckExternalQueryDetailRsp {

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("inspectionMonth")
    private LocalDateTime inspectionMonth;

//    @ApiModelProperty("项目名称")
//    private String projName;

    @ApiModelProperty("客户id")
    private Long clientId;

    @ApiModelProperty("客户Name")
    private String clientName;

    @ApiModelProperty("主办id")
    private Long sponsorUserId;

    @ApiModelProperty("主办名称")
    private String sponsorUserName;

//    @ApiModelProperty("项目主办用户id")
//    private Long projSponsorUserId;
//
//    @ApiModelProperty("项目主办用户Name")
//    private String projSponsorUserName;

    @ApiModelProperty("检查日期")
    private LocalDate inspectionDate;

    @ApiModelProperty("合同最终到期日")
    private LocalDate deadline;

    @ApiModelProperty("审批通过时间")
    private LocalDateTime approvalPassTime;

    @ApiModelProperty("状态")
    private String approvalStatus;

    @ApiModelProperty("租后检查部门")
    private Long deptId;

    @ApiModelProperty("租后检查部门名称")
    private String deptName;

    @ApiModelProperty("行业")
    private String industryType;

    @ApiModelProperty("风险敞口余额")
    private Long riskExposure;

    @ApiModelProperty("合同金额")
    private Long contractTotalAmount;

    @ApiModelProperty("下次还款日")
    private LocalDate nextRepayDate;

    @ApiModelProperty("下次还款金额")
    private Long nextRepayAmount;

    @ApiModelProperty("风险防范措施")
    private String preventiveMeasures;

    @ApiModelProperty("查询分析与查询结论")
    private String queryConclusion;

    @ApiModelProperty("查询分析与查询结论")
    private List<AfterLeaseCheckExternalQueryClientInfoListRsp> clientInfos;

    @ApiModelProperty("是否可以编辑")
    private boolean canModify;

    @ApiModelProperty("是否可以提交")
    private boolean canSubmit;
}
