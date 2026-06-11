package cn.zswltech.mithras.customer.application.validator;

import cn.zswltech.mithras.customer.constant.LackDataMsg;
import cn.zswltech.mithras.customer.mapper.model.client.NormalSpouse;
import org.apache.commons.lang3.StringUtils;

/**
 * 校验
 *
 * @author wangchuanhao
 * @date 2022/6/23 3:10 PM
 */
public class NormalSpouseValidator {

    public static void validate(NormalSpouse data) {
        CustomerValidatorSupport.errLackData(StringUtils.isBlank(data.getSpouseName()), LackDataMsg.SPOUSE_NAME);
        CustomerValidatorSupport.errLackData(StringUtils.isBlank(data.getCertNumber()), LackDataMsg.SPOUSE_CERT_NUMBER);
        CustomerValidatorSupport.errLackData(StringUtils.isBlank(data.getCertType()), LackDataMsg.SPOUSE_CERT_TYPE);
    }

}
