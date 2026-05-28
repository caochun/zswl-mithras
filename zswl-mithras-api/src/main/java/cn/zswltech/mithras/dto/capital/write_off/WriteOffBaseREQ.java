package cn.zswltech.mithras.dto.capital.write_off;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author bigbear
 * @date 2024/9/20 14:31
 * @description
 */
@Data
public class WriteOffBaseREQ {

    @NotBlank(message = "核销类型不能为空")
    @ApiModelProperty(value = "核销类型 WriteOffBusinessModelEnum#name")
    private String writeOffBusinessModel;
}
