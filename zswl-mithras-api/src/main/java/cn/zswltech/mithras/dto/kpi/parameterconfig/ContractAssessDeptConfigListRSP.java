package cn.zswltech.mithras.dto.kpi.parameterconfig;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author lizhao
 * @date 2024/6/19
 * @description
 */
@Data
@ApiModel("考核部门-参数设置-返回体")
public class ContractAssessDeptConfigListRSP {

    @ApiModelProperty(value = "考核部门-合同配置表id")
    private Long id;

    /**
     * 合同id
     */
    @ApiModelProperty(value = "合同id")
    private Long contractId;

    /**
     * 考核部门id
     */
    @ApiModelProperty(value = "考核部门")
    private Long assessDeptId;
}