package cn.zswltech.mithras.dto.fund.financing.plan;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2023/2/22
 * @description
 */
@Data
@ApiModel("融资管理-贷后变更-LPR调整-请求体")
public class FundFinancingChangeLprREQ {
    @ApiModelProperty("融资id")
    @NotNull(message = "融资id不能为空")
    private Long financingId;

    @ApiModelProperty("LPR类型")
    @NotBlank(message = "LPR类型不能为空")
    private String lprType;

    @ApiModelProperty("LPR利率")
    @NotNull(message = "LPR利率不能为空")
    private Integer lprRatePercent;

    @ApiModelProperty("LPR加点")
    @NotNull(message = "LPR加点不能为空")
    private Integer lprAddPercent;

    @ApiModelProperty("利率类型")
    @NotBlank(message = "利率类型不能为空")
    private String interestRateType;

    /**
     * LPR调整方式
     */
    @ApiModelProperty("LPR调整方式")
    private String lprArrangeMode;

    /**
     * LPR调整日
     */
    @ApiModelProperty("LPR调整日")
    private String lprAdjustmentDay;
}
