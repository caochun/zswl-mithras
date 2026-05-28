package cn.zswltech.mithras.dto.financeprojectdistribution;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description 新增项目分润部门分配比重表
 * @author hspcadmin
 * @date 2025-09-29
 */
@Data
@ApiModel("新增项目分润部门分配比重表-请求体")
public class FinanceProjectDistributionDeptWeightAddREQ {

    /**
    * 项目分配表id
    */
    @ApiModelProperty(value = "项目分配表id")
    private Long projectDistributionId;

    /**
    * 分配比重类型
    */
    @ApiModelProperty(value = "分配比重类型")
    private String weightType;

    /**
    * 分配比重归属目标
    */
    @ApiModelProperty(value = "分配比重归属目标")
    private String weightTarget;

    /**
    * 分配比重数值
    */
    @ApiModelProperty(value = "分配比重数值")
    private Integer wightValue;

}
