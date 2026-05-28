package cn.zswltech.mithras.dto.afterlease;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2022/11/18
 * @description
 */
@Data
@ApiModel("租后检查计划-非季度计划选择项目（更改为客户）-请求体")
public class AfterLeaseCheckClientSelectREQ {
    @NotNull(message = "检查计划id不能为空")
    @ApiModelProperty("检查计划id")
    private Long planId;

    @ApiModelProperty("客户名称")
    private String clientName;

//    @ApiModelProperty("项目名称")
//    private String projectName;
}
