package cn.zswltech.mithras.dto.finance.overdue;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @description 应收逾期集成表
 * @author vico
 * @date 2025-09-15
 */
@Data
@ApiModel("应收逾期集成表推送-请求体")
public class FinanceOverdueIntegrationPushREQ {

    /**
    * 收款明细id
    */
    @ApiModelProperty(value = "逾期报送计划id")
    @NotNull(message = "推送计划不能为空")
    private Long reportBaseId;

    /**
    * 收款编号
    */
    @ApiModelProperty(value = "推送id")
    private List<String> ids;


}
