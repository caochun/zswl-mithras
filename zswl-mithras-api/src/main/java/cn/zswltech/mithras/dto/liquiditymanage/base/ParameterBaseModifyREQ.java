package cn.zswltech.mithras.dto.liquiditymanage.base;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * ParameterBaseREQ
 *
 * @author chenyifei
 * @since 2024/12/16
 */
@Data
@ApiModel(value = "基础参数配置编辑请求体")
public class ParameterBaseModifyREQ {

    @ApiModelProperty(value = "安全库存")
    private Long saveStock;

    @ApiModelProperty(value = "灵活授信")
    private Long flexibleCredit;

}
