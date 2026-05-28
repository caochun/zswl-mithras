package cn.zswltech.mithras.dto.kpi;

import cn.zswltech.mithras.dto.kpi.parameterconfig.ContractAssessDeptDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author lizhao
 * @date 2024/6/18
 * @description
 */
@Data
@ApiModel("考核部门-参数设置-请求体")
public class ContractAssessDeptConfigListREQ {

    @ApiModelProperty("考核部门-合同配置表id")
    private List<Long> ids;

    /**
     * 合同id
     */
    @ApiModelProperty(value = "合同id")
    private List<Long> contractId;

    /**
     * 考核部门id
     */
    @ApiModelProperty(value = "考核部门")
    private Long assessDeptId;

    @ApiModelProperty(value = "请求体list")
    private List<ContractAssessDeptDto> contractAssessDeptlDtoList;



}