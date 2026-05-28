package cn.zswltech.mithras.dto.fund.financing.payaccount;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

/**
 * @ClassName FundFinancingPayAccountBankREQ
 * @Description TODO
 * @Author jackerhe
 * @Date 2023/2/21 10:04 上午
 * @Version 1.0
 **/
@Data
@ApiModel("融资管理-创建还款账户-请求体")
public class FundFinancingPayAccountModifyREQ {

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


    /**
     * 账户类别
     */
    @ApiModelProperty("账户类别")
    @NotBlank(message = "账户类别不能为空")
    private String accountCategory;

}
