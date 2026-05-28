package cn.zswltech.mithras.dto.contract.rent;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2022/8/18
 * @description
 */
@Data
@ApiModel("导入实际租金表-请求体")
public class ContractRentActualImportREQ {
    @NotNull(message = "导入文件不能为空")
    @ApiModelProperty("导入的文件")
    private MultipartFile file;

    @ApiModelProperty("合同id")
    @NotNull(message = "主合同id不能为空")
    private Long contractId;

    @ApiModelProperty("借据id")
    private Long receiptId;

    @ApiModelProperty("付款id")
    private Long paymentId;

    @ApiModelProperty("变更日期")
    private String changeDate;

    @ApiModelProperty("借据起租日期")
    private String receiptStartDate;
}
