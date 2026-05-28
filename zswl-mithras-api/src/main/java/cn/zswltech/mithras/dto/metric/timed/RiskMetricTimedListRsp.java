package cn.zswltech.mithras.dto.metric.timed;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @author yibin
 */
@Data
@ApiModel("风险指标定时存数-列表-结果")
public class RiskMetricTimedListRsp {

    @ApiModelProperty("数据时点")
    private LocalDate dataTime;

    @ApiModelProperty("项目名称")
    private String projName;

    @ApiModelProperty("项目类型")
    private String projType;

    @ApiModelProperty("行业名称")
    private String industryTypeName;

    @ApiModelProperty("五级分类名称")
    private String level5Type;

    @ApiModelProperty("客户名称")
    private String clientName;

    @ApiModelProperty("所属集团名称")
    private String belongGroupName;

    @ApiModelProperty("是否关联方")
    private Boolean related;

    @ApiModelProperty("剩余本金")
    private Long leftCapital;

    @ApiModelProperty("逾期金额")
    private Long overdueTotal;

}
