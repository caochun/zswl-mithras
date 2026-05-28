package cn.zswltech.mithras.dto.kpi;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
/**
 * @description 绩效考核-项目利润明细-记录表
 * @author vico
 * @date 2024-09-25
 */
@Data
@ApiModel("绩效考核-项目利润明细-记录表删除-请求体")
public class KpiFinanceProjectProfitRecordRemoveREQ {

    @NotNull
    @ApiModelProperty("id")
    private Long id;

}
