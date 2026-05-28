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
@ApiModel("ecl_预测业务配置表列表-返回体")
public class EclPredictBusinessConfigListRSP {

    /**
     * id
     */
    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * 配置模块
     */
    @ApiModelProperty(value = "配置模块")
    private String configModule;

    /**
     * 配置code
     */
    @ApiModelProperty(value = "配置code EclConfigEnum")
    private String configCode;

    /**
     * 配置名称
     */
    @ApiModelProperty(value = "配置名称 EclConfigEnum")
    private String configName;

    /**
     * 配置详情
     */
    @ApiModelProperty(value = "配置详情")
    private String configValue;

}
