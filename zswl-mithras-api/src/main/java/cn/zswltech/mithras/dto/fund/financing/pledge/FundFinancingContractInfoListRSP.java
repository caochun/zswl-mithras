package cn.zswltech.mithras.dto.fund.financing.pledge;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author bigbear
 * @date 2025/4/14 15:16
 * @description
 */
@Data
@ApiModel(value = "融资管理-合同信息-返回体")
public class FundFinancingContractInfoListRSP {

    @ApiModelProperty("业务部门id")
    private Long bizDeptId;

    @ApiModelProperty("业务部门名称")
    private String bizDeptName;

    @ApiModelProperty("客户id")
    private Long clientId;

    @ApiModelProperty("客户名称")
    private String clientName;

    @ApiModelProperty("项目评审id")
    private Long projReviewId;

    @ApiModelProperty("项目名称")
    private String projName;

    @ApiModelProperty("合同id")
    private Long contractId;

    @ApiModelProperty("合同编号")
    private String contractCode;

    @ApiModelProperty("业务类型。租赁、保理、转租赁")
    private String bizType;

    @ApiModelProperty("合同金额")
    private Long applyCreditAmount;

    @ApiModelProperty("实际起租日")
    private LocalDate actualLeaseDate;

    @ApiModelProperty("合同结清时间")
    private LocalDateTime settleTime;

    @ApiModelProperty("剩余未还本金")
    private Long remainingUnpaidPrincipal;
}
