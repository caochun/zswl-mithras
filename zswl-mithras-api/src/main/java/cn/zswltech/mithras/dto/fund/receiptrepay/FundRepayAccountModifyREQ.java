package cn.zswltech.mithras.dto.fund.receiptrepay;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @author zhaozhengkang
 * @description 资金管理-融资管理-我方付款账户
 * @date 2023-02-22
 */
@Data
@ApiModel("资金管理-收付款管理-我方付款账户编辑-请求体")
public class FundRepayAccountModifyREQ {

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("银行名称")
    private String accountBank;

    @ApiModelProperty("基础数据-我方银行账户id")
    private Long bankAccountId;

    @ApiModelProperty("账户类型")
    private String accountType;

    @ApiModelProperty("账号")
    private String accountNumber;

    @ApiModelProperty("开户时间")
    private LocalDate accountOpeningDate;

}
