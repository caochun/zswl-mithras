package cn.zswltech.mithras.dto.kpi;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author dingqi
 * @date 2023/6/14
 * @description
 */
@Data
@ApiModel("绩效考核-项目分配-基本信息-返回参数")
public class KpiProjectDistributionGetProcessRSP {
    @ApiModelProperty("流程ID")
    private String processInstanceId;
}
