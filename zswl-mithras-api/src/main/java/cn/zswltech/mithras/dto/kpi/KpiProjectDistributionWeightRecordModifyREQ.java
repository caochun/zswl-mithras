package cn.zswltech.mithras.dto.kpi;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
/**
 * @description 绩效考核-项目分配表-分配比重信息记录表
 * @author vico
 * @date 2024-09-27
 */
@Data
@ApiModel("绩效考核-项目分配表-分配比重信息记录表编辑-请求体")
public class KpiProjectDistributionWeightRecordModifyREQ {

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
