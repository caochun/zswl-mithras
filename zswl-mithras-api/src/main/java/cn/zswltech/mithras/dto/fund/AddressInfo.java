package cn.zswltech.mithras.dto.fund;

import lombok.Data;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/12/14 10:05
 */
@Data
public class AddressInfo {
    // 国家
    private String country;
    private String countryName;
    // 省
    private String province;
    private String provinceName;
    // 市
    private String city;
    private String cityName;
    // 区
    private String district;
    private String districtName;
    // 详细地址
    private String detail;
}
