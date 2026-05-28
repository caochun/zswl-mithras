package cn.zswltech.mithras.dto.client.addressinfo;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * @author luyi
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CorpAddressInfoListREQ extends PageReq {
    @ApiModelProperty("客户id")
    @NotNull
    private Long clientId;

    @ApiModelProperty(value = "发起人", required = true)
    private Long startUserId;

    /*@ApiModelProperty(value = "客户详情入口")
    private Boolean isClientDetail = Boolean.FALSE;*/
}
