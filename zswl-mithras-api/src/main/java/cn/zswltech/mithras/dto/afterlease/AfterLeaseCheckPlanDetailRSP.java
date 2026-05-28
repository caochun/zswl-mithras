package cn.zswltech.mithras.dto.afterlease;

import cn.zswltech.mithras.dto.ListBaseRSP;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author dingqi
 * @date 2022/11/14
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("租后管理-租后检查计划详情-返回体")
public class AfterLeaseCheckPlanDetailRSP extends ListBaseRSP {
    @ApiModelProperty("计划名称")
    private String planName;

    @ApiModelProperty("计划类型")
    private String planType;

    @ApiModelProperty("年份")
    private Integer year;

    @ApiModelProperty("季度")
    private Integer quarter;

    @ApiModelProperty("月份")
    private Integer month;

    @ApiModelProperty("检查完毕项目数")
    private Integer finishCount;

    @ApiModelProperty("计划包含项目数")
    private Integer totalCount;

    @ApiModelProperty("检查开始时间")
    private String checkStartDate;

    @ApiModelProperty("检查结束时间")
    private String checkEndDate;

    @ApiModelProperty("计划状态")
    private String planStatus;
}
