package cn.zswltech.mithras.dto.dashboard;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.List;

/**
 * @author chenyifei
 */
@Data
@ApiModel(value = "资金成本请求体")
@EqualsAndHashCode(callSuper = true)
public class DashboardFundFinanceFundsREQ extends DashboardFundFinanceBaseREQ {

    @ApiModelProperty("机构名称")
    private String orgName;

    @ApiModelProperty("融资类别")
    private String financingTypeCode;

    @ApiModelProperty("融资编号")
    private String financingCode;

    @ApiModelProperty("起息日开始")
    private LocalDate startDate;

    @ApiModelProperty("起息日结束")
    private LocalDate endDate;

    private List<Long> ids;
}
