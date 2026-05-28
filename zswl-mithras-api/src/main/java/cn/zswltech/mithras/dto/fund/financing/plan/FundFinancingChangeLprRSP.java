package cn.zswltech.mithras.dto.fund.financing.plan;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author dingqi
 * @date 2023/2/22
 * @description
 */
@Data
public class FundFinancingChangeLprRSP {
    @ApiModelProperty("原数据")
    private Data oldData;

    @ApiModelProperty("新数据")
    private Data newData;

    @lombok.Data
    public static class Data {
        @ApiModelProperty("借款年利率类型")
        private String interestRateType;

        @ApiModelProperty("LPR类型")
        private String lprType;

        @ApiModelProperty("LPR利率")
        private Integer lprRatePercent;

        @ApiModelProperty("LPR加点")
        private Integer lprAddPercent;
    }
}
