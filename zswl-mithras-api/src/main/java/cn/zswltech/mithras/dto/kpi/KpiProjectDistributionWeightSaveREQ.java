package cn.zswltech.mithras.dto.kpi;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author dingqi
 * @date 2023/6/15
 * @description
 */
@Data
@ApiModel("绩效考核-项目分配-分配比重-保存-请求参数")
public class KpiProjectDistributionWeightSaveREQ {
    @ApiModelProperty("项目分配id")
    private Long projectDistributionId;

    @ApiModelProperty("生效年份")
    private Integer year;

    @ApiModelProperty("生效月份")
    private Integer month;

    @ApiModelProperty("分配比重信息")
    private List<KpiProjectDistributionWeightInfo> weightInfoList;
}
