package cn.zswltech.mithras.service.validator;

import cn.zswltech.mithras.customer.domain.constant.LackDataMsg;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.CorpRelatedEnterprise;
import cn.zswltech.mithras.service.others.Util;
import org.apache.commons.lang3.StringUtils;

/**
 * 校验
 *
 * @author wangchuanhao
 * @date 2022/6/23 3:10 PM
 */
public class CorpRelatedEnterpriseValidator {

    public static void validate(CorpRelatedEnterprise data) {
        Util.errLackData(StringUtils.isBlank(data.getRelationship()), LackDataMsg.RELATED_ENTERPRISE_RELATIONSHIP);
        Util.errLackData(StringUtils.isBlank(data.getEnterpriseName()), LackDataMsg.RELATED_ENTERPRISE_ENTERPRISE_NAME);
    }

}
