package cn.zswltech.mithras.dto.capital;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2025/3/29
 * @description
 */
@Data
public class FinanceRepaySplitRSP {
    @ApiModelProperty("唯一KEY")
    private String uniqKey;
    @ApiModelProperty("融资id")
    private Long financingId;
    @ApiModelProperty("融资编号")
    private String financingCode;
    @ApiModelProperty("证券代码")
    private String securitiesCode;
    @ApiModelProperty("证券简称")
    private String abbreviation;
    @ApiModelProperty("拆分前的现金流编号")
    private String cashFlowCode;
    @ApiModelProperty("还款计划拆分后的明细id")
    private Long splitId;
    @ApiModelProperty("还款计划拆分后的明细编号")
    private String splitCashFlowCode;
    @ApiModelProperty("现金流类型，用于区分本金还是利息")
    private String cashFlowItem;
    @ApiModelProperty("期项")
    private Integer phase;
    @ApiModelProperty("应付日期")
    private LocalDate planRepayDate;
    @ApiModelProperty("应付金额（毫厘）")
    private Long planRepayAmount;
    @ApiModelProperty("未付金额（毫厘）")
    private Long remainingAmount;
}
