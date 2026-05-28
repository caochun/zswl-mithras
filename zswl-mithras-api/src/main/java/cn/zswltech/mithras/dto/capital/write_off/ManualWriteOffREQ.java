package cn.zswltech.mithras.dto.capital.write_off;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

/**
 * @author bigbear
 * @date 2024/9/19 18:50
 * @description
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "手动核销请求参数")
public class ManualWriteOffREQ extends WriteOffBaseREQ{

    @ApiModelProperty(value = "批次号")
    @NotBlank(message = "批次号不能为空")
    private String batchNumber;

}
