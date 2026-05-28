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
@ApiModel("ecl_预测业务配置表编辑-请求体")
public class EclPredictBusinessConfigDetailREQ {

    /**
    * id
    */
    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "预测id")
    private Long executePredictId;

    /**
     * 配置code
     */
    @ApiModelProperty(value = "配置code EclConfigEnum")
    private String configCode;

}
