package cn.zswltech.mithras.dto.kpi;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
/**
 * @description 绩效考核-部门-项目投放分配比重表
 * @author hspcadmin
 * @date 2025-09-29
 */
@Data
@ApiModel("绩效考核-部门-项目投放分配比重表列表-返回体")
public class KpiProjectDistributionDeptLaunchWeightListRSP {

    /**
    * 主键id
    */
    @ApiModelProperty(value = "主键id")
    private Long id;

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
