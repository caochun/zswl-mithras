package cn.zswltech.mithras.dto.capital;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;

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
