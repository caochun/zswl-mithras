package cn.zswltech.mithras.dto.projpricing.report;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2022/8/1
 * @description
 */
@Data
@ApiModel("生成报告-请求体")
public class ProjPricingReportGenerateREQ {
    @NotNull(message = "不能为空")
    @ApiModelProperty("项目定价id")
    private Long projPricingId;
}
