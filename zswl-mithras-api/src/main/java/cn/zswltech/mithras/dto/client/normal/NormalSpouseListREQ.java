package cn.zswltech.mithras.dto.client.normal;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * @author junke
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel("自然人配偶详情-请求体")
@Data
public class NormalSpouseListREQ extends PageReq {
    @NotNull
    @ApiModelProperty("客户id")
    private Long clientId;
}
