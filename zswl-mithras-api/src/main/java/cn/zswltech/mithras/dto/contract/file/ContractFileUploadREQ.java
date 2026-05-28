package cn.zswltech.mithras.dto.contract.file;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2022/8/15
 * @description
 */
@Data
@ApiModel("上传合同文件-请求体")
public class ContractFileUploadREQ {
    @NotNull(message = "上传文件不能为空")
    @ApiModelProperty("合同文件")
    private MultipartFile file;

    @NotNull(message = "主合同id不能为空")
    @ApiModelProperty("主合同id")
    private Long contractId;

    @NotBlank(message = "合同类型不能为空")
    @ApiModelProperty("合同类型")
    private String contractType;
}
