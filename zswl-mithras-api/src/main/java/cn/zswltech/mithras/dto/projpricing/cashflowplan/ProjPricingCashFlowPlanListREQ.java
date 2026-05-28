package cn.zswltech.mithras.dto.projpricing.cashflowplan;

import cn.zswltech.mithras.dto.VersionBaseREQ;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2022/8/1
 * @description
 */
@Data
@ApiModel("获取现金流计划表-请求体")
@AllArgsConstructor
@NoArgsConstructor
public class ProjPricingCashFlowPlanListREQ extends VersionBaseREQ {
    @NotNull(message = "项目定价记录id不能为空")
    @ApiModelProperty("项目定价记录id")
    private Long id;

    @ApiModelProperty("流程id")
    private String processInstanceId;
}
