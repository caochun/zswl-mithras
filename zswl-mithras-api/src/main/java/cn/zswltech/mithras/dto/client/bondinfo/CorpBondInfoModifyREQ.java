package cn.zswltech.mithras.dto.client.bondinfo;

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
@ApiModel("发债及评级信息变更-请求体")
public class CorpBondInfoModifyREQ extends CorpBondInfoAddREQ {
    @NotNull
    @ApiModelProperty("id")
    private Long id;
}
