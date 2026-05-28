package cn.zswltech.mithras.dto.kpi;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2023/6/14
 * @description
 */
@Data
@ApiModel("绩效考核-项目分配-基本信息-修改-请求参数")
public class KpiProjectDistributionBaseInfoModifyREQ {
    @ApiModelProperty("项目分配id")
    @NotNull(message = "项目分配id不能为空")
    private Long projectDistributionId;

    @ApiModelProperty("利润归属部门id")
    @NotNull(message = "利润归属部门不能为空")
    private Long profitBelongDeptId;

    @ApiModelProperty("团队长用户id")
    @NotNull(message = "团队长不能为空")
    private Long teamLeaderId;

    @ApiModelProperty("项目交接备注")
    @NotBlank(message = "项目交接备注不能为空")
    private String remark;

    @ApiModelProperty("说明")
    private String suppleDescribe;
}
