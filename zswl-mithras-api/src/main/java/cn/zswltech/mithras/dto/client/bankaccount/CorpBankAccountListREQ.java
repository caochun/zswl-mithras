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
public class CorpBankAccountListREQ extends PageReq {
    @NotNull
    @ApiModelProperty("客户id")
    private Long clientId;

    @ApiModelProperty(value = "发起人", required = true)
    private Long startUserId;

    /*@ApiModelProperty(value = "客户详情入口")
    private Boolean isClientDetail = Boolean.FALSE;*/
}
