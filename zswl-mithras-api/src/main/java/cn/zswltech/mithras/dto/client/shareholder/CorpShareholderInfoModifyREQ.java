package cn.zswltech.mithras.dto.client.shareholder;

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
@ApiModel("股东信息修改-请求体")
public class CorpShareholderInfoModifyREQ extends CorpShareholderInfoAddREQ {
    @NotNull
    @ApiModelProperty("id")
    private Long id;
}
