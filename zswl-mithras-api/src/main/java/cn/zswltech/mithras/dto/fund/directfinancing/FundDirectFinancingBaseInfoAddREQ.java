package cn.zswltech.mithras.dto.fund.directfinancing;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author zhaozhengkang
 * @description 直接融资-详情信息
 * @date 2023-06-17
 */
@Data
@ApiModel("直接融资-详情信息新增-请求体")
public class FundDirectFinancingBaseInfoAddREQ {

    @ApiModelProperty(value = "产品名称")
    @NotNull(message = "产品名称不能为空")
    private String productName;

    @ApiModelProperty(value = "融资金额")
    @NotNull(message = "融资金额不能为空")
    private Long financingAmount;

    @ApiModelProperty(value = "项目类型， 取下拉列表directFinancingType")
    @NotNull(message = "项目类型不能为空")
    private String directFinancingType;
}
