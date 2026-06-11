package cn.zswltech.mithras.liquidity.mapper.model.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 基础参数配置
 *
 * @author chenyifei
 * @since 2024/12/18
 */
@Data
public class FundParameterBaseConfigBO {

    @ApiModelProperty(value = "安全库存")
    private Long saveStock;

    @ApiModelProperty(value = "灵活授信")
    private Long flexibleCredit;
}
