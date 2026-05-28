package cn.zswltech.mithras.dto.projpricing.cashflowplan;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2022/8/9
 * @description
 */
@Data
@ApiModel("现金流量表下载-请求体")
public class ProjPricingCashFlowPlanDownloadREQ {
    @NotNull(message = "不能为空")
    @ApiModelProperty("项目定价记录id")
    private Long projPricingId;
}
