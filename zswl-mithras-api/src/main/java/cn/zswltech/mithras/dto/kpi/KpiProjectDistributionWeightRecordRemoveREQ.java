package cn.zswltech.mithras.dto.kpi;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
/**
 * @description 绩效考核-项目分配表-分配比重信息记录表
 * @author vico
 * @date 2024-09-27
 */
@Data
@ApiModel("绩效考核-项目分配表-分配比重信息记录表删除-请求体")
public class KpiProjectDistributionWeightRecordRemoveREQ {

    @NotNull
    @ApiModelProperty("id")
    private Long id;

}
