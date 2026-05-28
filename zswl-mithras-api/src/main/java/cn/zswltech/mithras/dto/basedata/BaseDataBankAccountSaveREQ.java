package cn.zswltech.mithras.dto.basedata;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2022/9/15
 * @description
 */
@Data
@ApiModel("保存我方账户-请求体")
public class BaseDataBankAccountSaveREQ {
    @ApiModelProperty("id")
    private Long id;

    @NotBlank(message = "账户类型不能为空")
    @ApiModelProperty("账户类型")
    private String accountType;

    @NotBlank(message = "账户名称不能为空")
    @ApiModelProperty("账户名称")
    private String accountName;

    @NotBlank(message = "银行账号不能为空")
    @ApiModelProperty("银行账号")
    private String accountNumber;

    @NotBlank(message = "开户银行不能为空")
    @ApiModelProperty("开户银行")
    private String accountBank;

    @NotNull(message = "是否贷款账户不能为空")
    @ApiModelProperty("是否贷款账户 0-否 1-是")
    private Integer isLoan;

    @NotNull(message = "账户状态不能为空")
    @ApiModelProperty("账户状态")
    private String accountStatus;

    @ApiModelProperty("开户时间 yyyy-MM-dd")
    private String openingDate;

    @ApiModelProperty("币种")
    private String currency;

    @ApiModelProperty("账户余额")
    private Long accountBalance;

    @ApiModelProperty("备注")
    private String remark;
}
