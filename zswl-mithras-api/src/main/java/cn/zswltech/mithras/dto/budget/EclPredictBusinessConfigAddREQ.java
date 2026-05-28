package cn.zswltech.mithras.dto.budget;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
/**
 * @description ecl_预测业务配置表
 * @author vico
 * @date 2025-10-14
 */
@Data
@ApiModel("ecl_预测业务配置表新增-请求体")
public class EclPredictBusinessConfigAddREQ {

    /**
    * 预测计划id
    */
    @ApiModelProperty(value = "预测计划id")
    private Long executePredictId;

    /**
    * 配置模块
    */
    @ApiModelProperty(value = "配置模块")
    private String configModule;

    /**
    * 配置code
    */
    @ApiModelProperty(value = "配置code")
    private String configCode;

    /**
    * 配置名称
    */
    @ApiModelProperty(value = "配置名称")
    private String configName;

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

}
