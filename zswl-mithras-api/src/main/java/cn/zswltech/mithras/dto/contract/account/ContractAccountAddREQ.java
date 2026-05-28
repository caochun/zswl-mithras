package cn.zswltech.mithras.dto.contract.account;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @description 合同-收款账户表
 * @author vico
 * @date 2022-08-12
 */
@Data
@ApiModel("合同-收款账户表新增-请求体")
public class ContractAccountAddREQ {

    /**
    * 所属合同id
    */
    @ApiModelProperty(value = "所属合同id")
    @NotNull(message = "合同id不能为空")
    private Long contractId;

    /**
    * 预留-客户id
    */
    @ApiModelProperty(value = "预留-客户id")
    private Long clientId;

    /**
    * 客户名称
    */
    @ApiModelProperty(value = "客户名称")
    private String clientName;

    /**
    * 账户名称
    */
    @ApiModelProperty(value = "账户名称")
    @NotNull(message = "账户名称不能为空")
    private String accountName;

    /**
    * 银行账号
    */
    @ApiModelProperty(value = "银行账号")
    @NotNull(message = "银行账号不能为空")
    private String accountNum;

    /**
    * 开户行
    */
    @ApiModelProperty(value = "开户行")
    @NotNull(message = "开户行不能为空")
    private String accountAddress;

    @ApiModelProperty(value = "回款方式 INDIRECT-间接还款 DIRECT-直接还款")
    private String repayWay;

    @ApiModelProperty(value = "账户用途")
    @NotBlank(message = "账户用途不能为空")
    private String accountUse;

    @ApiModelProperty(value = "收款人类型")
    private String payeeType;

    @ApiModelProperty(value = "我方账户id")
    private Long bankAccountId;

}
