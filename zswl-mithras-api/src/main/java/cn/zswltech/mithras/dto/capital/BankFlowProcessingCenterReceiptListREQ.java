package cn.zswltech.mithras.dto.capital;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author yangxiong
 * @date 2024/5/19/11:07
 * @description
 */
@Data
public class BankFlowProcessingCenterReceiptListREQ {

    /**
     * 合同ID
     */
    @ApiModelProperty(value = "合同ID")
    @NotNull(message = "合同ID不能为空")
    private Long contractId;
}
