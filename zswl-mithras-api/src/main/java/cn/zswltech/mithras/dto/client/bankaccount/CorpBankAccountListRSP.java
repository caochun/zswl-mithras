package cn.zswltech.mithras.dto.client.bankaccount;

import cn.zswltech.mithras.dto.ListBaseRSP;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author luyi
 */
@ApiModel("银行账号列表-返回体")
@Data
public class CorpBankAccountListRSP extends ListBaseRSP {

    @ApiModelProperty("客户账号")
    private Long clientId;

    /**
     * 是否主账号
     */
    @ApiModelProperty("是否主账号")
    private Boolean mainAccount;

    /**
     * 账号名称
     */
    @ApiModelProperty("账号名称")
    private String accountName;

    /**
     * 银行账号
     */
    @ApiModelProperty("银行账号")
    private String accountNumber;

    /**
     * 开户行
     */
    @ApiModelProperty("开户行")
    private String accountBank;

}
