package cn.zswltech.mithras.dto.afterlease;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 租金催收首页列表返回体
 *
 * @author wangchuanhao
 * @date 2022/11/17 3:09 PM
 */
@ApiModel("租金催收首页列表返回体")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClientUnifiedViewOverdueRentListRSP {

    @ApiModelProperty("付款id")
    private Long paymentId;

    @ApiModelProperty("主客户id")
    private Long clientId;

    @ApiModelProperty("合同id")
    private Long contractId;

    @ApiModelProperty("合同编号")
    private String contractCode;

    @ApiModelProperty(value = "计划收款日期")
    private LocalDate planCollectionDate;

    @ApiModelProperty(value = "期项")
    private Integer phase;

    @ApiModelProperty(value = "计划收款金额")
    private Long planCollectionAmount;

    @ApiModelProperty(value = "项目名称")
    private String projName;

    private Boolean overdueFlag;
}
