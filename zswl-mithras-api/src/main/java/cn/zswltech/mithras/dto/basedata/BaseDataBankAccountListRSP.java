package cn.zswltech.mithras.dto.basedata;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author dingqi
 * @date 2022/9/15
 * @description
 */
@Data
@ApiModel("基础数据-我方账户列表-返回体")
public class BaseDataBankAccountListRSP {
    @ApiModelProperty("账户id")
    private Long id;

    @ApiModelProperty("账户名称")
    private String accountName;

    @ApiModelProperty("账户类型")
    private String accountType;

    @ApiModelProperty("银行账号")
    private String accountNumber;

    @ApiModelProperty("开户银行")
    private String accountBank;

    @ApiModelProperty("是否贷款账户 0-否 1-是")
    private Integer isLoan;

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

    @ApiModelProperty("创建人id")
    private Long createUserId;

    @ApiModelProperty("创建人名称")
    private String createUserName;

    @ApiModelProperty("创建时间")
    private String createTime;

    @ApiModelProperty("修改时间")
    private String updateTime;
}
