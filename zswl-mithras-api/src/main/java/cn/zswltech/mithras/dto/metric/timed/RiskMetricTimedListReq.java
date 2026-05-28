package cn.zswltech.mithras.dto.metric.timed;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @author yibin
 */
@Data
@ApiModel("风险指标定时存数-列表-参数")
public class RiskMetricTimedListReq extends PageReq {

    @ApiModelProperty("数据时点")
    private LocalDate dataTime;

    @ApiModelProperty("项目类型")
    private String projType;

    @ApiModelProperty("行业分类")
    private String industryType;

    @ApiModelProperty("五级分类")
    private String level5Type;

    @ApiModelProperty("客户名称")
    private String clientName;

    @ApiModelProperty("所属集团")
    private String belongGroupName;

    @ApiModelProperty("是否关联方")
    private Boolean related;

}
