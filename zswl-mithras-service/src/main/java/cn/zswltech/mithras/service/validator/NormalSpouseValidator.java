package cn.zswltech.mithras.service.validator;

import cn.zswltech.mithras.service.constant.LackDataMsg;
import cn.zswltech.mithras.service.mapper.model.client.NormalSpouse;
import cn.zswltech.mithras.service.others.Util;
import org.apache.commons.lang3.StringUtils;

/**
 * 校验
 *
 * @author wangchuanhao
 * @date 2022/6/23 3:10 PM
 */
public class NormalSpouseValidator {

    public static void validate(NormalSpouse data) {
        Util.errLackData(StringUtils.isBlank(data.getSpouseName()), LackDataMsg.SPOUSE_NAME);
        Util.errLackData(StringUtils.isBlank(data.getCertNumber()), LackDataMsg.SPOUSE_CERT_NUMBER);
        Util.errLackData(StringUtils.isBlank(data.getCertType()), LackDataMsg.SPOUSE_CERT_TYPE);
    }

}
