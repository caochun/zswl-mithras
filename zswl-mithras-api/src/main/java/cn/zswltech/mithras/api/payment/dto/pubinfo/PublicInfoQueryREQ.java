package cn.zswltech.mithras.api.payment.dto.pubinfo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author bigbear
 * @date 2024/9/10 11:19
 * @description
 */
@Data
@ApiModel(value = "公开信息-查询结果请求体")
public class PublicInfoQueryREQ {

    @ApiModelProperty(value = "表格申请ID")
    @NotNull(message = "表格ID不能为空")
    private Long id;

}
