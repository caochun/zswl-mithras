package cn.zswltech.mithras.dto.contract.settle;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author dingqi
 * @date 2022/8/24
 * @description
 */
@Data
@ApiModel("合同结清补充协议上传-请求体")
public class ContractSettleExtraFileUploadREQ {
    @Size(max = 20, message = "最多支持上传20个补充协议文件")
    @NotEmpty(message = "补充协议文件不能为空")
    @ApiModelProperty("补充协议文件")
    private MultipartFile[] files;

    @NotNull(message = "主合同id不能为空")
    @ApiModelProperty("主合同id")
    private Long contractId;
}
