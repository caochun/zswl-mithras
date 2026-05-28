package cn.zswltech.mithras.dto.filingmaterials;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @author lllin
 */
@ApiModel("其他资料归档保持资料类型")
@Data
public class OtherSaveMaterialsDescREQ {
    @ApiModelProperty("id")
    private Long id;
    @ApiModelProperty("资料类型")
    private String materialsDesc;

}
