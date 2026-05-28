package cn.zswltech.mithras.dto.budget;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
/**
 * @description ecl_预测业务配置表
 * @author vico
 * @date 2025-10-14
 */
@Data
@ApiModel("ecl_预测业务配置表删除-请求体")
public class EclPredictBusinessConfigRemoveREQ {

    @NotNull
    @ApiModelProperty("id")
    private Long id;

}
