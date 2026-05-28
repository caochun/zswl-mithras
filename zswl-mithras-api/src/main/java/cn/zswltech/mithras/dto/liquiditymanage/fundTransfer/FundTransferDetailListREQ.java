package cn.zswltech.mithras.dto.liquiditymanage.fundTransfer;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;


/**
 * AccountBalanceListREQ
 *
 * @author chenyifei
 * @since 2024/12/16
 */
@Data
@ApiModel(value = "监管户待转资金列表请求参数")
public class FundTransferDetailListREQ {

    /**
     * id
     */
    @ApiModelProperty(value = "accountId")
    private Long accountId;

    /**
     * 开户银行
     */
    @ApiModelProperty(value = "开户银行")
    private String accountBank;

    /**
     * 起止日期-开始
     */
    @ApiModelProperty(value = "起止日期-开始")
    private LocalDate queryDateStart;

    /**
     * 起止日期-结束
     */
    @ApiModelProperty(value = "起止日期-结束")
    private LocalDate queryDateEnd;

}
