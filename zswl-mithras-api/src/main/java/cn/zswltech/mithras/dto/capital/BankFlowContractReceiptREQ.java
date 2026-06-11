package cn.zswltech.mithras.dto.capital;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @author yangxiong
 * @date 2024/5/18/14:49
 * @description
 */
@Data
public class BankFlowContractReceiptREQ {

    @ApiModelProperty(value = "合同id")
    private Long contractId;

}
