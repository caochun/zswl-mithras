package cn.zswltech.mithras.dto.fund.receiptrepay;

import cn.zswltech.mithras.dto.PageReq;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.List;

/**
 * @author zhaozhengkang
 * @description 收付款
 * @date 2023-02-20
 */
@Data
@ApiModel("收付款列表-请求体")
@EqualsAndHashCode(callSuper = true)
public class FundReceiptRepayBaseInfoListREQ extends PageReq {
    @ApiModelProperty(value = "融资机构")
    private Long financingOrgId;
    @ApiModelProperty(value = "收付款编号")
    private String receiptRepayCode;
    @ApiModelProperty(value = "融资金额from")
    private Long financingAmountFrom;
    @ApiModelProperty(value = "融资金额to")
    private Long financingAmountTo;

    @ApiModelProperty(value = "业务类型")
    private List<String> financingBizType;

    @ApiModelProperty(value = "付款状态")
    private String receiptRepayState;

    @ApiModelProperty(value = "Date From")
    private LocalDate dateFrom;

    @ApiModelProperty(value = "Date To")
    private LocalDate dateTo;

    @ApiModelProperty(value = "资金经理")
    private Long fundManager;

    @ApiModelProperty(value = "创建时间from")
    private LocalDate createTimeFrom;
    @ApiModelProperty(value = "创建时间to")
    private LocalDate createTimeTo;
    @ApiModelProperty(value = "更新时间from")
    private LocalDate updateTimeFrom;
    @ApiModelProperty(value = "更新时间to")
    private LocalDate updateTimeTo;

    @ApiModelProperty(value = "还款月份 默认当前年度-当前月份 format: yyyy-MM")
    private String repayMonth;

}
