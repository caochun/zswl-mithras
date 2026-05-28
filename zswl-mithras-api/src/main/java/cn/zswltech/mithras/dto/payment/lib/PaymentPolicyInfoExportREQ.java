package cn.zswltech.mithras.dto.payment.lib;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@ApiModel("下载保单-请求体")
public class PaymentPolicyInfoExportREQ {
    private List<Long> policyIds;

    @NotNull(message = "付款id不能为空")
    private Long paymentId;
}
