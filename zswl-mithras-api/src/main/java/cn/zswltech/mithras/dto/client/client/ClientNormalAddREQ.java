package cn.zswltech.mithras.dto.client.client;

import cn.zswltech.mithras.validation.BanSpecialChar;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author junke
 */
@Data
public class ClientNormalAddREQ {
    @NotBlank
    @ApiModelProperty(value = "客户名称", required = true)
    @BanSpecialChar
    private String clientName;
    @NotBlank
    @ApiModelProperty(value = "证件类型", required = true)
    private String certType;
    @NotBlank
    @ApiModelProperty(value = "证件号码", required = true)
    @BanSpecialChar
    private String certNumber;
}
