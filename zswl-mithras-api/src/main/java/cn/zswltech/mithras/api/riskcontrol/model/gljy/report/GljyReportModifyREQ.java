package cn.zswltech.mithras.api.riskcontrol.model.gljy.report;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author yibin
 */
@Data
@ApiModel("关联交易修改-请求体")
public class GljyReportModifyREQ extends GljyReportAddREQ{

    @NotNull
    @ApiModelProperty("id")
    private Long id;
}
