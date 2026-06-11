package cn.zswltech.mithras.customer.application.validator;

import cn.zswltech.mithras.customer.constant.LackDataMsg;
import cn.zswltech.mithras.customer.mapper.model.client.CorpRelatedEnterprise;
import org.apache.commons.lang3.StringUtils;

/**
 * 校验
 *
 * @author wangchuanhao
 * @date 2022/6/23 3:10 PM
 */
public class CorpRelatedEnterpriseValidator {

    public static void validate(CorpRelatedEnterprise data) {
        CustomerValidatorSupport.errLackData(StringUtils.isBlank(data.getRelationship()), LackDataMsg.RELATED_ENTERPRISE_RELATIONSHIP);
        CustomerValidatorSupport.errLackData(StringUtils.isBlank(data.getEnterpriseName()), LackDataMsg.RELATED_ENTERPRISE_ENTERPRISE_NAME);
    }

}
