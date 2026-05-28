package cn.zswltech.mithras.dto.client.contactinfo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author luyi
 */
@Data
public class CorpContactInfoRemoveREQ {

    @NotNull
    @ApiModelProperty("id")
    private Long id;
}
