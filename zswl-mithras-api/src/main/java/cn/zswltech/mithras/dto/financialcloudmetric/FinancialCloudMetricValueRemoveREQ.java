package cn.zswltech.mithras.dto.financialcloudmetric;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
/**
 * @description 金融云指标
 * @author zhaozhengkang
 * @date 2023-04-12
 */
@Data
@ApiModel("金融云指标删除-请求体")
public class FinancialCloudMetricValueRemoveREQ {

    @NotNull
    @ApiModelProperty("id")
    private Long id;

}
