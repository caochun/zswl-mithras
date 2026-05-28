package cn.zswltech.mithras.dto.finance.overdue;
import lombok.Data;
import java.time.LocalDateTime;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
/**
 * @description 逾期报送计划表
 * @author vico
 * @date 2025-09-15
 */
@Data
@ApiModel("逾期报送计划表编辑-请求体")
public class FinanceOverdueReportBaseModifyREQ {

    /**
    * 主键id
    */
    @ApiModelProperty(value = "主键id")
    private Long id;

    /**
    * 计划月份
    */
    @ApiModelProperty(value = "计划月份")
    private LocalDateTime planDate;

    /**
    * 报送状态
    */
    @ApiModelProperty(value = "报送状态")
    private String reportStatus;

}
