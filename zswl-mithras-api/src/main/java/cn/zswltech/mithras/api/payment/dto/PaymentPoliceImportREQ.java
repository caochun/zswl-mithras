package cn.zswltech.mithras.api.payment.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

/**
 * 
 * @author: jackerhe 
 * @date: 2022/12/30 10:42 上午
 **/
@Data
@ApiModel("保单信息导入-请求体")
public class PaymentPoliceImportREQ {
    @NotNull(message = "导入文件不能为空")
    @ApiModelProperty("保单文件")
    private MultipartFile file;

    @NotNull(message = "付款id不能为空")
    @ApiModelProperty("付款id")
    private Long paymentId;

    @ApiModelProperty("保单id")
    private Long policyId;
}
