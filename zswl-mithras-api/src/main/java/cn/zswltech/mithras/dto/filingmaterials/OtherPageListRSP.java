package cn.zswltech.mithras.dto.filingmaterials;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author lllin
 */
@ApiModel("其他资料归档台账-列表")
@Data
public class OtherPageListRSP {
    @ApiModelProperty("index")
    private int index;
    @ApiModelProperty("id")
    private Long id;
    @ApiModelProperty("项目名称")
    private String projName;
    @ApiModelProperty("项目编号")
    private String projCode;
    @ApiModelProperty("客户名称")
    private String clientName;
    @ApiModelProperty("客户ID")
    private Long clientId;
    @ApiModelProperty("资料类型")
    private String materialsDesc;
    @ApiModelProperty("业务部门")
    private String deptName;
    @ApiModelProperty("业务部门id")
    private Long bizDeptId;
    @ApiModelProperty("发起人id")
    private Long createBy;
    @ApiModelProperty("发起人")
    private String createByName;
    @ApiModelProperty("审批状态")
    private String approveStatus;
    @ApiModelProperty("发起时间")
    private LocalDateTime startDate;
    @ApiModelProperty("结束时间")
    private LocalDateTime approveDate;
}
