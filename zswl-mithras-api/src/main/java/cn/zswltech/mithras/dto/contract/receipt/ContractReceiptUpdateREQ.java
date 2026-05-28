package cn.zswltech.mithras.dto.contract.receipt;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2022/8/22
 * @description
 */
@Data
@ApiModel("合同管理-修改借据-请求体")
public class ContractReceiptUpdateREQ {
    @NotNull(message = "借据id不能为空")
    @ApiModelProperty("借据id")
    private Long receiptId;

    @NotNull(message = "主合同id不能为空")
    @ApiModelProperty("主合同id")
    private Long contractId;

    @NotBlank(message = "付款申请编号不能为空")
    @ApiModelProperty("付款申请编号")
    private String paymentApplyCode;

    @ApiModelProperty("实际租金表文件")
    private MultipartFile file;
}
