package cn.zswltech.mithras.dto.riskcontrol;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/3/13 16:08
 */
@ApiModel(value = "风控管理-关联方交易分页查询请求")
@Data
public class RiskControlRelatedTransactionPageReq extends PageReq {
    @ApiModelProperty(value = "客户名称")
    private String clientName;
    @ApiModelProperty(value = "关联方类型")
    private String relatedPartyType;
    @ApiModelProperty(value = "合同编号 ")
    private String contractCode;
    @ApiModelProperty(value = "交易金额from")
    private Long transactionAmountFrom;
    @ApiModelProperty(value = "交易金额to")
    private Long transactionAmountTo;
    @ApiModelProperty(value = "交易日期From")
    private LocalDate transactionDateFrom;
    @ApiModelProperty(value = "交易日期To")
    private LocalDate transactionDateTo;

}
