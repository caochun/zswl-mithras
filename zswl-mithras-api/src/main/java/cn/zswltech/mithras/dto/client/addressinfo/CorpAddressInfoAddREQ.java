package cn.zswltech.mithras.dto.client.addressinfo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author luyi
 */
@Data
@ApiModel("法人地址新增-请求体")
public class CorpAddressInfoAddREQ {


    @NotNull
    @ApiModelProperty("客户id")
    private Long clientId;
    @NotBlank
    @ApiModelProperty(value = "地址类型", required = true)
    private String addressType;
    @NotBlank
    @ApiModelProperty(value = "国别", required = true)
    private String country;
    @ApiModelProperty(value = "省份，国内时必填")
    private String province;
    @ApiModelProperty("城市，国内时必填")
    private String city;
    @ApiModelProperty("区/县，国内时必填")
    private String district;
    @ApiModelProperty("详细地址，国内时必填")
    private String detail;
    @ApiModelProperty("行政区域代码，国内时必填")
    private String regionCode;

}
