package cn.zswltech.mithras.dto.finance.overdue;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
/**
 * @description 应收逾期结算表
 * @author vico
 * @date 2025-09-15
 */
@Data
@ApiModel("应收逾期结算表删除-请求体")
public class FinanceOverdueSettlementRemoveREQ {

    @NotNull
    @ApiModelProperty("id")
    private Long id;

}
