package cn.zswltech.mithras.dto.dashboard;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2024/6/27
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DashboardProjectPayListRSP extends DashboardProjectBasicRSP {
    @ApiModelProperty("投放日期")
    private LocalDate actualPayDate;
    @ApiModelProperty("合同编号")
    private String contractCode;
    @ApiModelProperty("风险策略")
    private String riskStrategy;
    @ApiModelProperty("投放金额")
    private ValueUnitDTO actualPayAmount;
    @ApiModelProperty("项目期限")
    private ValueUnitDTO duration;
    @ApiModelProperty("IRR")
    private ValueUnitDTO actualIrr;
    @ApiModelProperty("合同利率")
    private ValueUnitDTO interestRate;
    @ApiModelProperty("咨询费率")
    private ValueUnitDTO consultingFeeRate;
    @ApiModelProperty("手续费率")
    private ValueUnitDTO commissionRate;
    @ApiModelProperty("保证金")
    private ValueUnitDTO earnest;
    @ApiModelProperty("地区code")
    private String regionalProjectClassifyCode;
    @ApiModelProperty("地区display")
    private String regionalProjectClassifyDisplay;
    // 20241024 新增借据编号，数据维度发生变化
    private Long receiptId;
    private String receiptCode;
    private Long paymentId;
    private String paymentCode;
}
