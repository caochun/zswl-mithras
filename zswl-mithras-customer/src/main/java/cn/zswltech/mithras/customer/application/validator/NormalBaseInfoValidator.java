package cn.zswltech.mithras.customer.application.validator;

import cn.zswltech.mithras.customer.constant.LackDataMsg;
import cn.zswltech.mithras.customer.mapper.model.client.NormalBaseInfo;
import org.apache.commons.lang3.StringUtils;

/**
 * 校验
 *
 * @author wangchuanhao
 * @date 2022/6/23 3:10 PM
 */
public class NormalBaseInfoValidator {

    public static void validate(NormalBaseInfo data) {
        CustomerValidatorSupport.errLackData(StringUtils.isBlank(data.getCertType()), LackDataMsg.NORMAL_BASE_CERT_TYPE);
        CustomerValidatorSupport.errLackData(StringUtils.isBlank(data.getCertNumber()), LackDataMsg.NORMAL_BASE_CERT_NUMBER);
        CustomerValidatorSupport.errLackData(StringUtils.isBlank(data.getGender()), LackDataMsg.NORMAL_BASE_GENDER);
        CustomerValidatorSupport.errLackData(StringUtils.isBlank(data.getMarriageType()), LackDataMsg.NORMAL_BASE_MARRIAGE_TYPE);
        CustomerValidatorSupport.errLackData(StringUtils.isBlank(data.getCountry()), LackDataMsg.NORMAL_BASE_COUNTRY);
        //CustomerValidatorSupport.errLackData(StringUtils.isBlank(data.getMobileNumber()), LackDataMsg.NORMAL_BASE_MOBILE_NUMBER);
    }

}
