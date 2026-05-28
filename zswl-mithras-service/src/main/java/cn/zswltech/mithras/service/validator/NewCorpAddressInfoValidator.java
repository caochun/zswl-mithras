package cn.zswltech.mithras.service.validator;

import cn.zswltech.mithras.service.constant.LackDataMsg;
import cn.zswltech.mithras.service.mapper.model.client.CorpAddressInfo;
import cn.zswltech.mithras.service.mapper.model.client.NewCorpAddressInfo;
import cn.zswltech.mithras.service.others.Const;
import cn.zswltech.mithras.service.others.Util;
import org.apache.commons.lang3.StringUtils;

/**
 * 校验
 *
 * @author wangchuanhao
 * @date 2022/6/23 3:10 PM
 */
public class NewCorpAddressInfoValidator {

    public static void validate(NewCorpAddressInfo data) {
        Util.errLackData(StringUtils.isBlank(data.getCountry()), LackDataMsg.ADDRESS_COUNTRY);
        if (Const.ADDRESS_INFO_COUNTRY_CHINA.equals(data.getCountry())) {
            // 国别为 中国必填下列信息
            Util.errLackData(StringUtils.isBlank(data.getProvince()), LackDataMsg.ADDRESS_PROVINCE);
            Util.errLackData(StringUtils.isBlank(data.getCity()), LackDataMsg.ADDRESS_CITY);
            Util.errLackData(StringUtils.isBlank(data.getDistrict()), LackDataMsg.ADDRESS_DISTRICT);
            Util.errLackData(StringUtils.isBlank(data.getDetail()), LackDataMsg.ADDRESS_DETAIL);
            Util.errLackData(StringUtils.isBlank(data.getRegionCode()), LackDataMsg.ADDRESS_REGION_CODE);
        }
    }

}
