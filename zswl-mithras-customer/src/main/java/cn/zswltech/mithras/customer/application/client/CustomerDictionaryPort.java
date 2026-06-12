package cn.zswltech.mithras.customer.application.client;

import lombok.Data;

import java.util.Collection;
import java.util.Map;

public interface CustomerDictionaryPort {

    Map<String, String> addressCode2Display(Collection<String> addressCodes);

    String addressCode2Display(String addressCode);

    TycAddress resolveTycAddress(String tycProvinceCode, String cityName, String districtName);

    @Data
    class TycAddress {
        private String provinceCode;
        private String cityCode;
        private String districtCode;
        private String regionCode;
    }
}
