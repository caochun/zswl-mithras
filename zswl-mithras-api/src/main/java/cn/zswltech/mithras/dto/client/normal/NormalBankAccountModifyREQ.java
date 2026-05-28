package cn.zswltech.mithras.dto.client.normal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * @author junke
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("自然人配偶信息修改-请求体")
public class NormalBankAccountModifyREQ extends NormalBankAccountAddREQ {

    @NotNull
    @ApiModelProperty("id")
    private Long id;

}
