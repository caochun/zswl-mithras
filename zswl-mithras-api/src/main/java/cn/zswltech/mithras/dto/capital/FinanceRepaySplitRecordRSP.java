package cn.zswltech.mithras.dto.capital;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2025/4/8
 * @description
 */
@Data
public class FinanceRepaySplitRecordRSP {
    @ApiModelProperty("核销明细id")
    private Long id;
    @ApiModelProperty("拆分前现金流编号")
    private String cashFlowCode;
    @ApiModelProperty("拆分后现金流编号")
    private String splitCashFlowCode;
    @ApiModelProperty("融资id")
    private Long financingId;
    @ApiModelProperty("融资编号")
    private String financingCode;
    @ApiModelProperty("证券代码")
    private String securitiesCode;
    @ApiModelProperty("证券简称")
    private String abbreviation;
    @ApiModelProperty("现金流类型")
    private String cashFlowItem;
    @ApiModelProperty("核销金额")
    private Long writeOffAmount;
    @ApiModelProperty("期项")
    private Integer phase;
    @ApiModelProperty("应付日期")
    private LocalDate planRepayDate;
}
