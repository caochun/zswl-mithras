package cn.zswltech.mithras.dto.leaseholdproperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author yupengfei
 * @date 2024/5/8 17:28
 */
@Data
public class LeaseVatInvoiceUploadREQ {

    @ApiModelProperty(value = "发票id")
    private Long vatInvoiceId;

    @ApiModelProperty(value = "操作类型(REPLACE:替换发票)")
    private String operateType;

    @ApiModelProperty(value = "租赁物id")
    private Long leaseholdId;

    @ApiModelProperty(value = "文件列表")
    @NotEmpty(message = "文件列表不能为空")
    private List<MultipartFile> files;

    @ApiModelProperty(value = "发票类型")
    private String invoiceType;
}
