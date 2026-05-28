package cn.zswltech.mithras.api.payment.dto.pubinfo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author bigbear
 * @date 2024/9/10 17:57
 * @description
 */
@Data
@ApiModel(value = "公开信息-导出请求体")
public class PublicInfoExportREQ {

    @ApiModelProperty(value = "付款申请ID")
    @NotNull(message = "付款申请ID不能为空")
    private Long paymentId;
}
