package cn.zswltech.mithras.dto.client.normal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author junke
 */
@Data
@ApiModel("自然配偶信息创建-请求体")
public class NormalSpouseAddREQ {

    @ApiModelProperty(value = "客户id", required = true)
    @NotNull
    private Long clientId;

    @NotBlank
    @ApiModelProperty(value = "配偶姓名", required = true)
    private String spouseName;

    @NotBlank
    @ApiModelProperty(value = "证件类型", required = true)
    private String certType;


    @NotBlank
    @ApiModelProperty(value = "证件号码", required = true)
    private String certNumber;
}
