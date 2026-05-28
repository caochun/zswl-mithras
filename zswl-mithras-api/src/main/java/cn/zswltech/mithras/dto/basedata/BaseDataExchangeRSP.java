package cn.zswltech.mithras.dto.basedata;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author dingqi
 * @date 2025/9/24
 * @description
 */
@Data
public class BaseDataExchangeRSP {
    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "年份")
    private Integer targetYear;

    @ApiModelProperty(value = "月份")
    private Integer targetMonth;

    @ApiModelProperty(value = "汇率日期")
    private LocalDate targetDate;

    @ApiModelProperty(value = "币种")
    private String currency;

    @ApiModelProperty(value = "汇率")
    private BigDecimal exchangeRate;

    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;
}
