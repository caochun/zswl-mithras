package cn.zswltech.mithras.dto.budget;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
/**
 * @description 资产减值预测表
 * @author vico
 * @date 2025-10-14
 */
@Data
@ApiModel("资产减值预测表删除-请求体")
public class EclExecutePredictBaseInfoRemoveREQ {

    @NotNull
    @ApiModelProperty("id")
    private Long id;

}
