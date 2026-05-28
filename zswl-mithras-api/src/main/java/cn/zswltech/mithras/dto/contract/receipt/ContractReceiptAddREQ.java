package cn.zswltech.mithras.dto.contract.receipt;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2022/8/20
 * @description
 */
@Data
@ApiModel("新增借据-请求体")
public class ContractReceiptAddREQ {
    @NotNull(message = "主合同id不能为空")
    @ApiModelProperty("主合同id")
    private Long contractId;

    @NotBlank(message = "付款申请编号不能为空")
    @ApiModelProperty("付款申请编号")
    private String paymentApplyCode;

    @NotNull(message = "实际租金表不能为空")
    @ApiModelProperty("实际租金表文件")
    private MultipartFile file;
}
