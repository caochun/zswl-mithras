package cn.zswltech.mithras.dto.client.addressinfo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author luyi
 */
@Data
public class CorpAddressInfoRemoveREQ {
    @NotNull
    @ApiModelProperty(value = "地址id", required = true)
    private Long id;
}
