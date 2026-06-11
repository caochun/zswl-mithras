package cn.zswltech.mithras.dto.liquiditymanage.base;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;


/**
 * ParameterBaseRSP
 *
 * @author chenyifei
 * @since 2024/12/16
 */
@Data
@ApiModel(value = "基础参数配置详情返回体")
public class ParameterBaseDetailRSP {

    @ApiModelProperty(value = "安全库存")
    private Long saveStock;

    @ApiModelProperty(value = "灵活授信")
    private Long flexibleCredit;

    @ApiModelProperty(value = "账户余额更新时间")
    private LocalDateTime accountBalanceUpdateTime;

}
