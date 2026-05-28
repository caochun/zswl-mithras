package cn.zswltech.mithras.dto.report.specialtrade;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * 征信报送-特定交易表编辑入参
 *
 * @author wangchuanhao
 * @date 2023/1/11 11:12 AM
 */
@ApiModel("征信报送-特定交易表编辑入参")
@Data
public class SpecialTradeModifyREQ {

    @ApiModelProperty("id")
    @NotNull
    private Long id;

    @NotNull(message = "修改原因不能为空")
    @ApiModelProperty("修改原因")
    private String reason;

    @ApiModelProperty("交易类型")
    private String tradeType;

    @ApiModelProperty("交易日期")
    private LocalDate tradeDate;

    @ApiModelProperty("交易金额")
    private Long tradeAmount;

    @ApiModelProperty("交易变更月数(月)")
    private Integer changeMonthCount;

}
