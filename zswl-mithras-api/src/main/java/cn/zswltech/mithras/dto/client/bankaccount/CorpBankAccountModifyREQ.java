package cn.zswltech.mithras.dto.client.bankaccount;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * @author luyi
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("银行账号变更-请求体")
public class CorpBankAccountModifyREQ extends CorpBankAccountAddREQ {

    @NotNull
    @ApiModelProperty(value = "id", required = true)
    private Long id;
}
