package cn.zswltech.mithras.dto.kpi;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
/**
 * @description 绩效考核-参数设置基本表
 * @author vico
 * @date 2024-09-21
 */
@Data
@ApiModel("绩效考核-参数设置基本表删除-请求体")
public class KpiParameterBaseCommonREQ {

    @NotNull
    @ApiModelProperty("id")
    private Long id;

}
