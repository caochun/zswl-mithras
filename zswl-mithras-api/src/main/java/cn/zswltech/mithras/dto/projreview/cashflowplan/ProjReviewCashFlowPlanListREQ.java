package cn.zswltech.mithras.dto.projreview.cashflowplan;

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
public class ProjReviewCashFlowPlanListREQ extends VersionBaseREQ {
    @NotNull(message = "项目评审记录id不能为空")
    @ApiModelProperty("项目评审记录id")
    private Long id;

    @ApiModelProperty("流程id")
    private String processInstanceId;
}
