package cn.zswltech.mithras.dto.kpi;

import cn.zswltech.mithras.dto.ListBaseRSP;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @description 绩效考核-项目分配表-分配比重信息记录表
 * @author vico
 * @date 2024-09-27
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("绩效考核-项目分配表-分配比重信息记录表列表-返回体")
public class KpiProjectDistributionWeightRecordListRSP extends ListBaseRSP {

    /**
    * id
    */
    @ApiModelProperty(value = "id")
    private Long id;

    /**
    * 项目分配记录表id
    */
    @ApiModelProperty(value = "项目分配记录表id")
    private Long projectDistributionRecordId;

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

    private String weightTypeName;

    /**
    * 分配比重归属目标
    */
    @ApiModelProperty(value = "分配比重归属目标")
    private String weightTarget;

    private String weightTargetName;

    /**
    * 分配比重数值
    */
    @ApiModelProperty(value = "分配比重数值")
    private Integer weightValue;

}
