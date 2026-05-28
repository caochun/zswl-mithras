package cn.zswltech.mithras.dto.finance.overdue;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @description 应收逾期结算表
 * @author vico
 * @date 2025-09-15
 */
@Data
@ApiModel("应收逾期结算表提交-请求体")
public class FinanceOverdueVersionSubmitREQ {

    /**
    * 结算记录id
    */
    @ApiModelProperty(value = "集成记录id")
    private List<String> integrationRecordIds;

    /**
     * 结算记录id
     */
    @ApiModelProperty(value = "结算记录id")
    private List<String> settlementRecordIds;

    /**
    * 收款编号
    */
    @ApiModelProperty(value = "收款编号")
    @NotNull(message = "报告id不能为空")
    private Long reportId;


}
