package cn.zswltech.mithras.dto.client.bondinfo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author luyi
 */
@Data
@ApiModel("发债及评级信息删除-请求体")
public class CorpBondInfoRemoveREQ {
    @NotNull
    @ApiModelProperty("id")
    private Long id;
}
