package cn.zswltech.mithras.dto.financeprojectdistribution;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2023/6/15
 * @description
 */
@Data
@ApiModel("财务-项目分配-提交审批-请求参数")
public class FinanceProjectDistributionSubmitREQ {
    @ApiModelProperty("项目分配id")
    @NotNull(message = "项目分配id不能为空")
    private Long projectDistributionId;
}
