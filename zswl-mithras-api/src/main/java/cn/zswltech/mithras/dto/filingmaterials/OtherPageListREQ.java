package cn.zswltech.mithras.dto.filingmaterials;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;


@ApiModel("其他资料归档-入参")
@Data
public class OtherPageListREQ extends PageReq {
    @ApiModelProperty(value = "记录ID集合")
    private List<Long> ids;

    @ApiModelProperty("项目名称")
    private String projName;
    @ApiModelProperty("项目编号")
    private String projCode;
    @ApiModelProperty("客户名称")
    private String clientName;
    @ApiModelProperty("客户id")
    private Long clientId;
    @ApiModelProperty("资料类型")
    private String materialsDesc;
    @ApiModelProperty("业务部门id")
    private Long bizDeptId;
    @ApiModelProperty("发起人")
    private Long createById;
    @ApiModelProperty("审批状态")
    private String approveStatus;
    @ApiModelProperty("审批状态")
    private List<String> approveStatusList;
    @ApiModelProperty("发起时间开始")
    private LocalDate startDateFrom;
    @ApiModelProperty("发起时间开始")
    private LocalDate startDateTo;
    @ApiModelProperty("结束时间开始")
    private LocalDate endDateFrom;
    @ApiModelProperty("结束时间开始")
    private LocalDate endDateTo;
}
