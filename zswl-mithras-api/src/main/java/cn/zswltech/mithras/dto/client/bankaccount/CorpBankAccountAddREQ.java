package cn.zswltech.mithras.dto.client.bankaccount;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author luyi
 */
@ApiModel("银行账号新增-请求体")
@Data
public class CorpBankAccountAddREQ {

    @NotNull
    @ApiModelProperty(value = "客户账号", required = true)
    private Long clientId;

    /**
     * 是否主账号
     */
    @ApiModelProperty("是否主账号")
    private Boolean mainAccount;

    /**
     * 账号名称
     */
    @NotBlank
    @ApiModelProperty(value = "账号名称", required = true)
    private String accountName;

    /**
     * 银行账号
     */
    @NotBlank
    @ApiModelProperty(value = "银行账号", required = true)
    private String accountNumber;

    /**
     * 开户行
     */
    @NotBlank
    @ApiModelProperty(value = "开户行", required = true)
    private String accountBank;

}
