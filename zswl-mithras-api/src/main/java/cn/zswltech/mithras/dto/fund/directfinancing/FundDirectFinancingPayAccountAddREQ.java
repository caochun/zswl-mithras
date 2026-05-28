package cn.zswltech.mithras.dto.fund.directfinancing;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * @author zhaozhengkang
 * @description 直接融资-还款账户
 * @date 2023-06-17
 */
@Data
@ApiModel("直接融资-还款账户新增-请求体")
public class FundDirectFinancingPayAccountAddREQ {

    /**
     * 融资id
     */
    @NotNull(message = "融资id不能为空")
    @ApiModelProperty("融资id")
    private Long financingId;

    /**
     * 支行名称
     */
    @NotBlank(message = "银行名称不能为空")
    @ApiModelProperty("银行名称")
    private String accountBank;

    /**
     * 主键id
     */
    @NotNull(message = "我方账户id不能为空")
    @ApiModelProperty("基础数据-我方银行账户id")
    private Long bankAccountId;

    /**
     * 账户类型
     */
    @NotBlank(message = "账户性质不能为空")
    @ApiModelProperty("账户类型")
    private String accountType;


    /**
     * 账号
     */
    @NotBlank(message = "银行账号不能为空")
    @ApiModelProperty("账号")
    private String accountNumber;

    /**
     * 开户时间
     */
    @ApiModelProperty("开户时间")
    private LocalDate accountOpeningDate;

}
