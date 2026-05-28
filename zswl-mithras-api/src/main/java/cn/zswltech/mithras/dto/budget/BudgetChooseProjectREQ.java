package cn.zswltech.mithras.dto.budget;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author dingqi
 * @date 2025/6/17
 * @description
 */
@Data
public class BudgetChooseProjectREQ {
    @ApiModelProperty("部门id")
    private Long belongDeptId;
}
