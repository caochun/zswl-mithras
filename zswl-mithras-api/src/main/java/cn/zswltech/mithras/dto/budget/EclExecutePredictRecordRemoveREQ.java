package cn.zswltech.mithras.dto.budget;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
/**
 * @description 资产减值预测详情记录表
 * @author vico
 * @date 2025-10-14
 */
@Data
@ApiModel("资产减值预测详情记录表删除-请求体")
public class EclExecutePredictRecordRemoveREQ {

    @NotNull
    @ApiModelProperty("id")
    private Long id;

}
