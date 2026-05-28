package cn.zswltech.mithras.service.validator;

import cn.zswltech.mithras.service.constant.LackDataMsg;
import cn.zswltech.mithras.service.mapper.model.client.NormalBaseInfo;
import cn.zswltech.mithras.service.others.Util;
import org.apache.commons.lang3.StringUtils;

/**
 * 校验
 *
 * @author wangchuanhao
 * @date 2022/6/23 3:10 PM
 */
public class NormalBaseInfoValidator {

    public static void validate(NormalBaseInfo data) {
        Util.errLackData(StringUtils.isBlank(data.getCertType()), LackDataMsg.NORMAL_BASE_CERT_TYPE);
        Util.errLackData(StringUtils.isBlank(data.getCertNumber()), LackDataMsg.NORMAL_BASE_CERT_NUMBER);
        Util.errLackData(StringUtils.isBlank(data.getGender()), LackDataMsg.NORMAL_BASE_GENDER);
        Util.errLackData(StringUtils.isBlank(data.getMarriageType()), LackDataMsg.NORMAL_BASE_MARRIAGE_TYPE);
        Util.errLackData(StringUtils.isBlank(data.getCountry()), LackDataMsg.NORMAL_BASE_COUNTRY);
        //Util.errLackData(StringUtils.isBlank(data.getMobileNumber()), LackDataMsg.NORMAL_BASE_MOBILE_NUMBER);
    }

}
