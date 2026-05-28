package cn.zswltech.mithras.api.payment.dto.pubinfo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author bigbear
 * @date 2024/9/10 18:03
 * @description
 */
@Data
@ApiModel(value = "公开信息-创建请求体")
public class PublicInfoCreateREQ {

    @ApiModelProperty(value = "付款申请ID")
    @NotNull(message = "付款申请ID不能为空")
    private Long paymentId;

    @ApiModelProperty(value = "客户ID")
    @NotNull(message = "客户ID不能为空")
    private Long clientId;

    @ApiModelProperty(value = "客户类型")
    @NotBlank(message = "客户类型不能为空")
    private String clientType;

    @ApiModelProperty(value = "查询开始时间 format:yyyy-MM-dd")
    @NotBlank(message = "查询开始时间不能为空")
    private String queryFrom;

    @ApiModelProperty(value = "查询结束时间 format:yyyy-MM-dd")
    @NotBlank(message = "查询结束时间不能为空")
    private String queryTo;

}
