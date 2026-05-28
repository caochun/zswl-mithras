package cn.zswltech.mithras.dto.finance.overdue;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
/**
 * @description 逾期报送计划表
 * @author vico
 * @date 2025-09-15
 */
@Data
@ApiModel("逾期报送计划表删除-请求体")
public class FinanceOverdueReportBaseRemoveREQ {

    @NotNull
    @ApiModelProperty("id")
    private Long id;

}
