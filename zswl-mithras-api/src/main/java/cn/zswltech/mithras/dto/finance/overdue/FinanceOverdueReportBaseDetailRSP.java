package cn.zswltech.mithras.dto.finance.overdue;

import cn.zswltech.mithras.dto.ListBaseRSP;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @description 逾期报送计划表
 * @author vico
 * @date 2025-09-15
 */
@Data
@ApiModel("逾期报送计划表新增-请求体")
public class FinanceOverdueReportBaseDetailRSP extends ListBaseRSP {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private Long id;

    /**
     * 计划月份
     */
    @ApiModelProperty(value = "计划月份")
    private LocalDate planDate;

    /**
     * 报送状态
     */
    @ApiModelProperty(value = "报送状态")
    private String reportStatus;

}
