package cn.zswltech.mithras.dto.finance.overdue;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * @description 逾期报送计划表
 * @author vico
 * @date 2025-09-15
 */
@Data
@ApiModel("逾期报送计划表新增-请求体")
public class FinanceOverdueReportBaseAddREQ {

    /**
    * 计划月份
    */
    @NotNull(message = "计划月份不能为空")
    @ApiModelProperty(value = "计划月份")
    private LocalDate planDate;

}
