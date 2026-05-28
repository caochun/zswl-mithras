package cn.zswltech.mithras.api.payment.dto.pubinfo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author bigbear
 * @date 2024/9/10 18:00
 * @description
 */
@Data
@ApiModel(value = "公开信息-删除请求体")
public class PublicInfoDeleteREQ {

    @ApiModelProperty(value = "表格ID不能为空")
    @NotNull(message = "表格ID不能为空")
    private Long id;

}
