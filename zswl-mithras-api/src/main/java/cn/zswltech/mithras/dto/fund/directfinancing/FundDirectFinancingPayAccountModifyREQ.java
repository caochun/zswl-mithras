package cn.zswltech.mithras.dto.fund.directfinancing;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
/**
 * @description 直接融资-还款账户
 * @author zhaozhengkang
 * @date 2023-06-17
 */
@Data
@ApiModel("直接融资-还款账户编辑-请求体")
public class FundDirectFinancingPayAccountModifyREQ {

    @ApiModelProperty("id")
    private Long id;

    /**
     * 融资id
     */
    @ApiModelProperty("融资id")
    private Long financingId;

    /**
     * 支行名称
     */
    @ApiModelProperty("银行名称")
    private String accountBank;

    @ApiModelProperty("基础数据-我方银行账户id")
    private Long bankAccountId;

    /**
     * 账户类型
     */
    @ApiModelProperty("账户类型")
    private String accountType;


    /**
     * 账号
     */
    @ApiModelProperty("账号")
    private String accountNumber;

    /**
     * 开户时间
     */
    @ApiModelProperty("开户时间")
    private LocalDate accountOpeningDate;
}
