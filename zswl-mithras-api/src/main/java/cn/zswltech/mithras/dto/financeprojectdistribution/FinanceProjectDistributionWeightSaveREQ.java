package cn.zswltech.mithras.dto.financeprojectdistribution;

import cn.zswltech.mithras.dto.kpi.KpiProjectDistributionWeightInfo;
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
@ApiModel("财务-项目分配-分配比重-保存-请求参数")
public class FinanceProjectDistributionWeightSaveREQ {
    @ApiModelProperty("项目分配id")
    private Long projectDistributionId;

    @ApiModelProperty("生效年份")
    private Integer year;

    @ApiModelProperty("生效月份")
    private Integer month;

    @ApiModelProperty("分配比重信息")
    private List<KpiProjectDistributionWeightInfo> weightInfoList;
}
