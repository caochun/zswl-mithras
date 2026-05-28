package cn.zswltech.mithras.dto.metric.factor;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author dingqi
 * @date 2023/8/16
 * @description
 */
@ApiModel("财务报表-明细列表-返回参数")
@Data
public class RiskMetricFactorPageListRsp {
    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("金额")
    private Long factorValue;

    @ApiModelProperty("名称")
    private String factorName;

    @ApiModelProperty("表名")
    private String factorTable;
}
