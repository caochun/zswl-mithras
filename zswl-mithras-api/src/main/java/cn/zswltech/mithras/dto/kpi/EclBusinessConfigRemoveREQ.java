package cn.zswltech.mithras.dto.kpi;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
/**
 * @description ecl_业务配置表
 * @author vico
 * @date 2025-09-24
 */
@Data
@ApiModel("ecl_业务配置表删除-请求体")
public class EclBusinessConfigRemoveREQ {

    @NotNull
    @ApiModelProperty("id")
    private Long id;

}
