package cn.zswltech.mithras.api.payment.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author dingqi
 * @date 2023/12/14
 * @description
 */
@Data
public class PaymentAutoRegisterReq {
    @ApiModelProperty(value = "付款申请id")
    @NotNull(message = "付款申请id不能为空")
    private Long paymentId;

    @ApiModelProperty("文件")
    private List<MultipartFile> files;
}
