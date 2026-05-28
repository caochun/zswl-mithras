package cn.zswltech.mithras.dto.dashboard;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class DashboardContractReturnListREQ {

    @ApiModelProperty("时间开始")
    private LocalDate endTimeFrom;
    @ApiModelProperty("时间结束")
    private LocalDate endTimeTo;
    @ApiModelProperty("时间开始")
    private LocalDate queryDateFrom;
    @ApiModelProperty("时间结束")
    private LocalDate queryDateTo;
    @ApiModelProperty("业务部门id")
    private List<Long> bizDeptId;
    @ApiModelProperty("客户id")
    private Long clientId;
    @ApiModelProperty("项目名称")
    private String projName;

    @ApiModelProperty("合同编号")
    private String contractCode;

    @ApiModelProperty("运营经办id")
    private Long yunYingGuanLiId;

    @ApiModelProperty("运营复核id")
    private Long yunYingGuanLiReviewId;

    @ApiModelProperty("项目主办")
    private Long projSponsorUserId;

    @ApiModelProperty(value = "业务组类别 BusinessGroupEnum")
    private List<String> businessGroup;

    @ApiModelProperty(value = "业务模式 二级模式")
    private List<String> businessModel;

    private List<Long> contractIds;

}
