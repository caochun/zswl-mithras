package cn.zswltech.mithras.dto.capital;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author zhouning
 * @date 2024/7/16/13:31
 * @description
 */
@Data
public class BankFlowNettingRefundREQ {

    /**
     * 流水列表ID
     */
    @ApiModelProperty(value = "流水ID列表")
    @NotEmpty(message = "流水ID列表不能为空")
    private List<Long> financeFlowIds;


    /**
     * 轧差退款金额
     */
    @ApiModelProperty(value = "轧差退款金额")
    private String refundAmount;
}
