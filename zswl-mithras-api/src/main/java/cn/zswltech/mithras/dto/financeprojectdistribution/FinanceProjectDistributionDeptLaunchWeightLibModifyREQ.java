package cn.zswltech.mithras.dto.financeprojectdistribution;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @description 财务-部门-项目投放分配比重版本表
 * @author hspcadmin
 * @date 2025-09-29
 */
@Data
@ApiModel("财务-部门-项目投放分配比重版本表编辑-请求体")
public class FinanceProjectDistributionDeptLaunchWeightLibModifyREQ {

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

    /**
    * 版本号
    */
    @ApiModelProperty(value = "版本号")
    private String version;

    /**
    * 临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写
    */
    @ApiModelProperty(value = "临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写")
    private Long originId;

    /**
    * 原始数据创建时间
    */
    @ApiModelProperty(value = "原始数据创建时间")
    private LocalDateTime dataCreateTime;

    /**
    * 原始数据创建人id
    */
    @ApiModelProperty(value = "原始数据创建人id")
    private Long dataCreateBy;

    /**
    * 原始数据更新时间
    */
    @ApiModelProperty(value = "原始数据更新时间")
    private LocalDateTime dataUpdateTime;

    /**
    * 原始数据更新人id
    */
    @ApiModelProperty(value = "原始数据更新人id")
    private Long dataUpdateBy;

    /**
    * 版本标志，0无效，1有效...业务自扩展
    */
    @ApiModelProperty(value = "版本标志，0无效，1有效...业务自扩展")
    private Integer versionType;

}
