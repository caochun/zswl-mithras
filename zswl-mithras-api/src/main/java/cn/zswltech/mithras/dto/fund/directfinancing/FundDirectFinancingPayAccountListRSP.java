package cn.zswltech.mithras.dto.fund.directfinancing;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @author zhaozhengkang
 * @description 直接融资-还款账户
 * @date 2023-06-17
 */
@Data
@ApiModel("直接融资-还款账户列表-返回体")
public class FundDirectFinancingPayAccountListRSP {

    @ApiModelProperty("id")
    private Long id;

    /**
     * 主键id
     */
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
     * 支行名称
     */
    @ApiModelProperty("支行名称")
    private String accountBank;

    /**
     * 开户时间
     */
    @ApiModelProperty("开户时间")
    private LocalDate accountOpeningDate;

}
