package cn.zswltech.mithras.dto.dashboard;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

/**
 * @author dingqi
 * @date 2024/6/24
 * @description
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DashboardFundFinanceRepayREQ extends DashboardFundFinanceBaseREQ {
    private LocalDate repayDateFrom;
    private LocalDate repayDateTo;
    @ApiModelProperty("机构名称/产品名称")
    private String orgName;
    @ApiModelProperty("状态")
    private String writeOffState;
    @ApiModelProperty("核销状态（多值）")
    private List<String> writeOffStateList;
    @ApiModelProperty("融资编号")
    private String financingCode;
    private List<Long> ids;
}
