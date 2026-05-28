package cn.zswltech.mithras.dto.basedata;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2025/9/24
 * @description
 */
@Data
public class BaseDataExchangeREQ {
    @ApiModelProperty(value = "主键id")
    private Long id;

    @NotNull(message = "年份不能为空")
    @ApiModelProperty(value = "年份")
    private Integer targetYear;

    @NotNull(message = "月份不能为空")
    @ApiModelProperty(value = "月份")
    private Integer targetMonth;

    @NotNull(message = "汇率日期不能为空")
    @ApiModelProperty(value = "汇率日期")
    private LocalDate targetDate;

    @NotBlank(message = "币种不能为空")
    @ApiModelProperty(value = "币种")
    private String currency;

    @NotNull(message = "汇率不能为空")
    @ApiModelProperty(value = "汇率")
    private BigDecimal exchangeRate;

    @NotNull(message = "是否草稿数据标识不能为空")
    @ApiModelProperty(value = "是否草稿数据（待办中新增的先保存为草稿数据，需要传1）")
    private Integer isDraft;
}
