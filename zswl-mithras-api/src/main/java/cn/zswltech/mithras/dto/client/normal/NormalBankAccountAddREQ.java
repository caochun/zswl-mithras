package cn.zswltech.mithras.dto.client.normal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author junke
 */
@Data
@ApiModel("自然人银行账户新增-请求体")
public class NormalBankAccountAddREQ {

    /**
     * 账户名称
     */
    @NotBlank
    @ApiModelProperty(value = "账号名称", required = true)
    private String accountName;

    /**
     * 账号
     */
    @NotBlank
    @ApiModelProperty(value = "账号", required = true)
    private String accountNumber;

    /**
     * 开户行
     */
    @NotBlank
    @ApiModelProperty(value = "开户行", required = true)
    private String accountBank;

    /**
     * 是否主账号
     */
    @ApiModelProperty(value = "是否主账号")
    private Boolean mainAccount;

    /**
     * 客户id
     */
    @NotNull
    @ApiModelProperty(value = "客户id", required = true)
    private Long clientId;

}
