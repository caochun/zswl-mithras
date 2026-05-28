package cn.zswltech.mithras.dto.metric.factor;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @author yibin
 */
@Data
@ApiModel("风险指标因子-列表-参数")
public class RiskMetricFactorListReq extends PageReq {
    @ApiModelProperty(value = "因子-日期，传月份即可，日期调整到本月最后一日", required = true)
    private LocalDate factorDate;
    @ApiModelProperty("因子-sheet名称. e.g: 现金流量表")
    private String factorTable;
    @ApiModelProperty("因子名称。e.g:库存现金@年初余额@借方金额")
    private String factorName;

}
