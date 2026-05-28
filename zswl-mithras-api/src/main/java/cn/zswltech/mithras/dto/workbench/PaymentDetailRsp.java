package cn.zswltech.mithras.dto.workbench;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/6/10 13:56
 */
@Data
@ApiModel("工作台-大可视化付款核销列表")
public class PaymentDetailRsp {

    //合同编号 项目名称 类别 业务部门 投放金额 剩余应付 应付日期 实付日期

    @ApiModelProperty("项目名称")
    private String projectName;
    @ApiModelProperty("项目id")
    private Long projectId;
    @ApiModelProperty("合同id")
    private Long contractId;
    @ApiModelProperty("合同编号")
    private String contractCode;
    @ApiModelProperty("类别")
    private String bizType;
    @ApiModelProperty("业务部门")
    private String bizDeptName;
    @ApiModelProperty("投放金额")
    private Long paidAmount;
    @ApiModelProperty("剩余应付")
    private Long remainAmount;
    @ApiModelProperty("应付日期")
    private LocalDate planPayDate;
    @ApiModelProperty("实付日期")
    private LocalDate paidInDate;
}
