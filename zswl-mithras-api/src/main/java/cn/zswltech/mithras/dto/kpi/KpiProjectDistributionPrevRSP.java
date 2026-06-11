package cn.zswltech.mithras.dto.kpi;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @author dingqi
 * @date 2023/6/15
 * @description
 */
@Data
@ApiModel("绩效考核-项目分配-查看上个版本-返回参数")
public class KpiProjectDistributionPrevRSP {
    @ApiModelProperty("id")
    private Long id;

    private Long projectDistributionId;

    // 分配比重类型
    @ApiModelProperty("分配比重类型")
    private String weightType;

    // 分配比重类型名称
    @ApiModelProperty("分配比重类型名称")
    private String weightTypeName;

    // 分配比重目标
    @ApiModelProperty("分配比重目标")
    private Long weightTarget;

    // 分配比重目标名称
    @ApiModelProperty("分配比重目标名称")
    private String weightTargetName;

    // 分配比重值
    @ApiModelProperty("分配比重值")
    private Integer weightValue;
}
