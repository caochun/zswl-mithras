package cn.zswltech.mithras.api.riskcontrol.model.gljy.report;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yibin
 */
@Data
@ApiModel("关联交易关联方-请求体")
public class GljyReportRelatedClientREQ {
    @ApiModelProperty("关联方名称；支持模糊")
    private String name;
}
