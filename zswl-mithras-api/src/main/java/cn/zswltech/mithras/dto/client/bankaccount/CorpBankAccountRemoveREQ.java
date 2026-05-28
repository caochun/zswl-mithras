package cn.zswltech.mithras.dto.client.bankaccount;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author luyi
 */
@Data
public class CorpBankAccountRemoveREQ {
    @NotNull
    @ApiModelProperty(value = "id", required = true)
    private Long id;
}
