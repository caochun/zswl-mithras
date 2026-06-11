package cn.zswltech.mithras.dto.liquiditymanage.liquidityindex;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;


/**
 * LiquidityMismatchDetailRSP
 *
 * @author chenyifei
 * @since 2024/12/16
 */
@Data
@ApiModel(value = "流动性错配明细返回体")
public class LiquidityMismatchDetailRSP {

    @ApiModelProperty(value = "融资id")
    private Long financingId;

    @ApiModelProperty(value = "融资编号")
    private String financingCode;

    @ApiModelProperty(value = "融资机构id")
    private List<Long> organizationId;

    @ApiModelProperty(value = "融资机构名称")
    private List<String> organizationName;

    @ApiModelProperty(value = "期项")
    private Integer phase;

    @ApiModelProperty(value = "现金流出时间")
    private LocalDate cashOutflowTime;

    @ApiModelProperty(value = "现金流出金额")
    private Long cashOutflowAmount;

    @ApiModelProperty(value = "流入明细")
    private List<LiquidityMismatchCashInFlow> cashInFlowList;

    @ApiModelProperty(value = "现金流入时间")
    private LocalDate cashInflowTime;

    @ApiModelProperty(value = "现金流入金额")
    private Long cashInflowAmount;


    @Data
    public static class LiquidityMismatchCashInFlow {

        @ApiModelProperty(value = "质押/监管合同编号")
        private String pledgeContractCode;

        @ApiModelProperty(value = "现金流入时间")
        private LocalDate cashInflowTime;

        @ApiModelProperty(value = "现金流入金额")
        private Long cashInflowAmount;

    }

}
