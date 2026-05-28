package cn.zswltech.mithras.dto.afterlease;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * @description 租后检查外部查询任务
 * @author zhaozhengkang
 * @date 2022-11-17
 */
@Data
@ApiModel("外部查询任务编辑结论-请求体")
public class AfterLeaseCheckExternalQueryConclusionModifyReq {

    @ApiModelProperty(value = "主键id")
    @NotNull
    private Long id;

    @ApiModelProperty(value = "风险信号及重大事项、风险防范措施")
    private String preventiveMeasures;

    @ApiModelProperty(value = "查询分析与查询结论")
    private String queryConclusion;

}
