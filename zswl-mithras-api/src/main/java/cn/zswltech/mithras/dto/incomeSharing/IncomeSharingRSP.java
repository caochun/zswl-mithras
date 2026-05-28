package cn.zswltech.mithras.dto.incomeSharing;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDate;

/**
 * @author yupengfei
 * @date 2024/6/7 17:28
 */
@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class IncomeSharingRSP {

    @ApiModelProperty(value = "借据编号")
    private String receiptCode;

    @ApiModelProperty(value = "合同编号")
    private String contractCode;

    @ApiModelProperty(value = "日期")
    private LocalDate incomeDate;

    @ApiModelProperty(value = "长期应收款期初余额")
    private Long beginOfTermBalance;

    @ApiModelProperty(value = "长期应收款余额")
    private Long endOfTermBalance;

    @ApiModelProperty(value = "当天应收租金")
    private Long rent;

    @ApiModelProperty(value = "当天应收租金")
    private Long tax;

    @ApiModelProperty(value = "当天确认收入")
    private Long income;

    @ApiModelProperty(value = "不含税收入")
    private Long incomeWithoutTax;

    @ApiModelProperty(value = "日折现率")
    private String dailyDiscountRate;

    @ApiModelProperty(value = "收入是否确认")
    private Integer isConfirmed;
}
