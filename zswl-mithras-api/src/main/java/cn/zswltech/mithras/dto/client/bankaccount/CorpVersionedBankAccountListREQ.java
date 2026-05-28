package cn.zswltech.mithras.dto.client.bankaccount;

import cn.zswltech.mithras.dto.PageReq;
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
@ApiModel("银行账号列表-请求体")
public class CorpVersionedBankAccountListREQ extends PageReq {
    @NotNull
    @ApiModelProperty("客户id")
    private Long clientId;

    @ApiModelProperty("版本，不填默认取最新")
    private String version;
}
