package cn.zswltech.mithras.dto.dashboard;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;


@Data
public class DashboardProjectPlanStatisticsRSP {
    @ApiModelProperty("投放合同数量")
    private Integer payContractQuantity;

    @ApiModelProperty("实际投放金额")
    private ValueUnitDTO payAmount;

    @ApiModelProperty("计划投放金额")
    private ValueUnitDTO planPayAmount;

    @ApiModelProperty("达成率")
    private ValueUnitDTO finishRate;

    @ApiModelProperty("达成率排名")
    private Integer finishRateRank;

    @JsonIgnore
    private BigDecimal sortedField;
}
