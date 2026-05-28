package cn.zswltech.mithras.dto.leaseholdproperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yupengfei
 * @date 2024/7/10 11:06
 */
@Data
public class LeaseVehicleRegistrationUploadRSP {

    @ApiModelProperty("成功数量")
    private Integer success;

    @ApiModelProperty("成功数量")
    private Integer fail;

    // @ApiModelProperty("文件名")
    // private String fileNames;
}
