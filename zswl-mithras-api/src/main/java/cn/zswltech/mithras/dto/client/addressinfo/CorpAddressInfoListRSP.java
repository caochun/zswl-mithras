package cn.zswltech.mithras.dto.client.addressinfo;

import cn.zswltech.mithras.dto.ListBaseRSP;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author luyi
 */
@Data
@ApiModel("法人地址列表-返回体")
public class CorpAddressInfoListRSP extends ListBaseRSP {

    @ApiModelProperty("客户id")
    private Long clientId;
    @ApiModelProperty(value = "地址类型")
    private String addressType;
    @ApiModelProperty(value = "国别")
    private String country;

    @ApiModelProperty("国家名称")
    private String countryName;
    @ApiModelProperty(value = "省份，国内时必填")
    private String province;
    @ApiModelProperty("省份名称")
    private String provinceName;
    @ApiModelProperty("城市，国内时必填")
    private String city;
    @ApiModelProperty("城市名称")
    private String cityName;
    @ApiModelProperty("区/县，国内时必填")
    private String district;
    @ApiModelProperty("区/县名称")
    private String districtName;
    @ApiModelProperty("详细地址，国内时必填")
    private String detail;
    @ApiModelProperty("行政区域代码，国内时必填")
    private String regionCode;

}
