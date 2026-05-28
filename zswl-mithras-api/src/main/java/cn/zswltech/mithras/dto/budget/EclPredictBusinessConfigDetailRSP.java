package cn.zswltech.mithras.dto.budget;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description ecl_预测业务配置表
 * @author vico
 * @date 2025-10-14
 */
@Data
@ApiModel("ecl_预测业务配置表编辑-返回体")
public class EclPredictBusinessConfigDetailRSP {

    /**
    * id
    */
    @ApiModelProperty(value = "id")
    private Long id;


    /**
     * 配置code
     */
    @ApiModelProperty("配置code")
    private String configCode;

    /**
     * 配置详情
     */
    @ApiModelProperty(value = "配置详情")
    private String configValue;

    /**
     * 排序
     */
    @ApiModelProperty(value = "排序")
    private Integer orderFlag;

    @ApiModelProperty(value = "枚举")
    private String configEnum;

}
