package cn.zswltech.mithras.contract.mapper.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/10/24 10:15
 */
@Data
@ApiModel("逾期催收详情-合同信息返回")
public class OcContractDto {
    @ApiModelProperty("还款id")
    private Long id;

    @ApiModelProperty("合同id")
    private Long contractId;

    @ApiModelProperty("合同编号")
    private String contractCode;

    @ApiModelProperty(value = "客户id")
    private String clientId;

    @ApiModelProperty(value = "期次")
    private String phase;

    @ApiModelProperty(value = "逾期金额")
    private Long overdueAmount;

    @ApiModelProperty(value = "逾期天数")
    private Integer overdueDays;

}
