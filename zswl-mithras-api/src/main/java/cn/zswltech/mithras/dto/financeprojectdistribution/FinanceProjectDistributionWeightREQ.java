package cn.zswltech.mithras.dto.financeprojectdistribution;

import cn.zswltech.mithras.dto.VersionBaseREQ;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2023/6/15
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("财务-项目分配-分配比重-列表-请求参数")
public class FinanceProjectDistributionWeightREQ extends VersionBaseREQ {
    @ApiModelProperty("项目分配id")
    @NotNull(message = "项目分配id不能为空")
    private Long projectDistributionId;
}
