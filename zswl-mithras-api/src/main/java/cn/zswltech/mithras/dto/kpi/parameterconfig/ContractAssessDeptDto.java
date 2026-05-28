package cn.zswltech.mithras.dto.kpi.parameterconfig;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author lizhao
 * @date 2024/6/19
 * @description 考核部门-合同配置表
 */
@Data
public class ContractAssessDeptDto {

    @ApiModelProperty("考核部门-合同配置表id")
    private Long id;

    /**
     * 合同id
     */
    @ApiModelProperty("合同id")
    private Long contractId;


    /**
     * 考核部门id
     */
    @ApiModelProperty("考核部门id")
    private Long assessDeptId;
}
