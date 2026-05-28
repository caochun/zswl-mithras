package cn.zswltech.mithras.dto.dashboard;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class DashboardApprovalListREQ {

    @ApiModelProperty("时间开始")
    private LocalDate endTimeFrom;
    @ApiModelProperty("时间结束")
    private LocalDate endTimeTo;

    @ApiModelProperty("时间开始")
    private LocalDate queryDateFrom;
    @ApiModelProperty("时间结束")
    private LocalDate queryDateTo;

    private List<Long> bizDeptId;

    @ApiModelProperty("客户id")
    private Long clientId;
    @ApiModelProperty("项目名称")
    private String projName;

    @ApiModelProperty("合同编号")
    private String contractCode;


    @ApiModelProperty("项目主办")
    private Long projSponsorUserId;

    @ApiModelProperty(value = "业务组类别 BusinessGroupEnum")
    private String businessGroup;

    @ApiModelProperty(value = "业务模式 二级模式")
    private String businessModel;

    private List<Long> contractIds;

}
