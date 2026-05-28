package cn.zswltech.mithras.dto.client.normal;

import cn.zswltech.mithras.dto.ListBaseRSP;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author junke
 */
@Data
@ApiModel("自然人银行账户列表-返回体")
public class NormalBankAccountListRSP extends ListBaseRSP {

    /**
     * 账户名称
     */
    @ApiModelProperty(value = "账号名称")
    private String accountName;

    /**
     * 账号
     */
    @ApiModelProperty(value = "账号")
    private String accountNumber;

    /**
     * 开户行
     */
    @ApiModelProperty(value = "开户行")
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
