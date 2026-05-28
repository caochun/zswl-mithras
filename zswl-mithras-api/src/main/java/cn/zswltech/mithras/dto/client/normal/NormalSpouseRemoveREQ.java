package cn.zswltech.mithras.dto.client.normal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author junke
 */
@ApiModel("自然人配偶删除-请求体")
@Data
public class NormalSpouseRemoveREQ {

    @NotNull
    @ApiModelProperty("id")
    private Long id;
}
