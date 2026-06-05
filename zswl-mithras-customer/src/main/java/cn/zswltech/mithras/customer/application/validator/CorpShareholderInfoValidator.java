package cn.zswltech.mithras.customer.application.validator;

import cn.zswltech.mithras.customer.domain.constant.LackDataMsg;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.CorpShareholderInfo;
import org.apache.commons.lang3.StringUtils;

/**
 * 校验
 *
 * @author wangchuanhao
 * @date 2022/6/23 3:10 PM
 */
public class CorpShareholderInfoValidator {

    public static void validate(CorpShareholderInfo data) {
        CustomerValidatorSupport.errLackData(StringUtils.isBlank(data.getShareholderType()), LackDataMsg.SHAREHOLDER_TYPE);
        CustomerValidatorSupport.errLackData(StringUtils.isBlank(data.getShareholderName()), LackDataMsg.SHAREHOLDER_NAME);
    }

}
