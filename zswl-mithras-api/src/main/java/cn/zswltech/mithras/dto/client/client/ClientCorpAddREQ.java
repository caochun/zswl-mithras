package cn.zswltech.mithras.dto.client.client;

import cn.zswltech.mithras.validation.BanSpecialChar;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author junke
 */
@Data
public class ClientCorpAddREQ {
    @NotBlank
    @ApiModelProperty(value = "客户名称", required = true)
    @BanSpecialChar
    private String clientName;
    @NotBlank
    @ApiModelProperty(value = "境内or境外，取枚举domesticOrAbroad（境内DOMESTIC、境外ABROAD）", required = true)
    private String domesticOrAbroad;
    @ApiModelProperty(value = "社会统一信用代码，境内客户")
    @BanSpecialChar
    private String uscCode;
    @ApiModelProperty(value = "特殊机构代码，境外客户")
    private String specialOrgCode;
    @ApiModelProperty("忽略天眼查失败，直接保存")
    private Boolean muteTycError;
}
