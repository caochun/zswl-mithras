package cn.zswltech.mithras.dto.fund;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @author zhaozhengkang
 * @description fund_credit
 * @date 2022-12-13
 */
@Data
@ApiModel("授信列表-请求体")
public class FundCreditListREQ extends PageReq {
    @ApiModelProperty(value = "授信机构")
    private Long organizationId;
    @ApiModelProperty(value = "授信机构类型")
    private String organizationType;
    @ApiModelProperty(value = "授信额度From")
    private Long creditLimitFrom;
    @ApiModelProperty(value = "授信额度To")
    private Long creditLimitTo;
    @ApiModelProperty(value = "授信日期From")
    private LocalDate creditDateFrom;
    @ApiModelProperty(value = "授信日期To")
    private LocalDate creditDateTo;
    @ApiModelProperty(value = "创建人")
    private Long createBy;
    @ApiModelProperty(value = "创建时间From")
    private LocalDate createTimeFrom;
    @ApiModelProperty(value = "创建时间To")
    private LocalDate createTimeTo;
    @ApiModelProperty(value = "是否生效")
    private Boolean effective;
}
