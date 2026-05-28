package cn.zswltech.mithras.dto.capital;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author dingqi
 * @date 2025/4/8
 * @description
 */
@Data
public class FinanceRepaySplitRecordREQ {
    @NotEmpty(message = "拆分后的实际还款现金流编号不能为空")
    @ApiModelProperty("拆分后的实际还款现金流编号")
    private List<String> cashFlowCodeList;
}
