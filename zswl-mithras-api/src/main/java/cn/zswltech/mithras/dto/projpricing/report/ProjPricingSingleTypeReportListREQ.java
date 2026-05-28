package cn.zswltech.mithras.dto.projpricing.report;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2022/8/1
 * @description
 */
@Data
@ApiModel("报告清单列表-请求体")
public class ProjPricingSingleTypeReportListREQ {
    @NotNull(message = "不能为空")
    @ApiModelProperty("项目定价记录id")
    private Long projPricingId;

    @NotBlank(message = "资料类型不能为空")
    @ApiModelProperty("资料类型")
    private String materialsType;
}
