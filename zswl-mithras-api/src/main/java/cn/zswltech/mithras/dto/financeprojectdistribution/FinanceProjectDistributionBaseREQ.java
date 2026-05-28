package cn.zswltech.mithras.dto.financeprojectdistribution;

import cn.zswltech.mithras.dto.VersionBaseREQ;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2023/6/14
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("项目利润分配-基本信息-请求参数")
public class FinanceProjectDistributionBaseREQ extends VersionBaseREQ {
    @ApiModelProperty("项目利润分配id")
    @NotNull(message = "项目利润分配id不能为空")
    private Long projectDistributionId;
}
