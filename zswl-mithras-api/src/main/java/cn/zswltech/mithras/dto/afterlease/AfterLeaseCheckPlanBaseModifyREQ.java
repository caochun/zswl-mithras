package cn.zswltech.mithras.dto.afterlease;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2022/11/23
 * @description
 */
@Data
@ApiModel("租后管理-修改租后检查计划基本信息-请求体")
public class AfterLeaseCheckPlanBaseModifyREQ {
    @ApiModelProperty("计划id")
    @NotNull(message = "计划id不能为空")
    private Long id;

    @ApiModelProperty("计划名称")
    @NotBlank(message = "计划名称不能为空")
    private String planName;

    @ApiModelProperty("检查所属年度")
    @NotNull(message = "检查所属年度不能为空")
    private Integer year;

    @ApiModelProperty("检查所属季度")
    private Integer quarter;

    @ApiModelProperty("检查所属月份")
    private Integer month;

    @ApiModelProperty("检查填报开始时间 yyyy-MM-dd")
    @NotBlank(message = "检查填报开始时间不能为空")
    private String startDate;

    @ApiModelProperty("检查填报结束时间 yyyy-MM-dd")
    @NotBlank(message = "检查填报结束时间不能为空")
    private String endDate;

    @ApiModelProperty("主办id")
    private Long sponsorId;

    @ApiModelProperty("检查形式")
    private String checkWay;

    @ApiModelProperty("协查风控经理")
    private Long riskManagerId;

}
