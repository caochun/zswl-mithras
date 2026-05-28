package cn.zswltech.mithras.dto.contract.account;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @description 合同-收款账户表
 * @author vico
 * @date 2022-08-12
 */
@Data
@ApiModel("合同-收款账户表编辑-请求体")
public class ContractAccountModifyREQ {

    /**
    * 方案id
    */
    @ApiModelProperty(value = "方案id")
    @NotNull(message = "id不能为空")
    private Long id;

    @ApiModelProperty(value = "合同id")
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
    private String accountName;

    /**
    * 银行账号
    */
    @ApiModelProperty(value = "银行账号")
    private String accountNum;

    /**
    * 开户行
    */
    @ApiModelProperty(value = "开户行")
    private String accountAddress;

    @ApiModelProperty(value = "回款方式")
    private String repayWay;

    @ApiModelProperty(value = "账户用途")
    @NotBlank(message = "账户用途不能为空")
    private String accountUse;

    @ApiModelProperty(value = "收款人类型")
    private String payeeType;

    @ApiModelProperty(value = "我方账户id")
    private Long bankAccountId;

}
