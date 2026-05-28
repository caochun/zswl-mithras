package cn.zswltech.mithras.dto.finance.overdue;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
/**
 * @description 应收逾期集成表
 * @author vico
 * @date 2025-09-15
 */
@Data
@ApiModel("应收逾期集成表删除-请求体")
public class FinanceOverdueIntegrationRemoveREQ {

    @NotNull
    @ApiModelProperty("id")
    private Long id;

}
