package cn.zswltech.mithras.dto.afterlease;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2022/11/14
 * @description
 */
@Data
@ApiModel("租后管理-发布检查计划-请求体")
public class AfterLeaseCheckPlanPublishREQ {
    @ApiModelProperty("检查计划id")
    @NotNull(message = "检查计划id不能为空")
    private Long planId;
}
