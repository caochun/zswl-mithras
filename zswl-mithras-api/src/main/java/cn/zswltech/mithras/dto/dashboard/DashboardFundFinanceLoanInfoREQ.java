package cn.zswltech.mithras.dto.dashboard;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.List;

/**
 * @author dingqi
 * @date 2024/6/24
 * @description
 */
@Data
@ApiModel(value = "融资情况请求参数")
@EqualsAndHashCode(callSuper = true)
public class DashboardFundFinanceLoanInfoREQ extends DashboardFundFinanceBaseREQ {

    @ApiModelProperty(value = "融资类别")
    private String financingTypeCode;

    @ApiModelProperty(value = "机构名称/产品名称")
    private String orgName;

    private List<Long> ids;

    @ApiModelProperty(value = "起息日是否在本年")
    private Integer isThisYear;

    @ApiModelProperty(value = "起息日是否在本月")
    private Integer isThisMonth;

    private LocalDate actualLoanDateFrom;
    private LocalDate actualLoanDateTo;
    private List<String> financingStatusList;
}
