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
@ApiModel("绩效考核-项目分配-查看历史-返回参数")
public class KpiProjectDistributionHistoryRSP {
    @ApiModelProperty("历史版本号")
    private String version;

    @ApiModelProperty("操作日期")
    private String operateDate;

    @ApiModelProperty("分润比重列表")
    private List<KpiProjectDistributionWeightInfoWithTag> weightInfoWithTagList;

    @ApiModelProperty("生效年份")
    private Integer effectYear;

    @ApiModelProperty("生效月份")
    private Integer effectMonth;

    @ApiModelProperty("变更原因")
    private String changeReason;
}
