package cn.zswltech.mithras.customer.application.validator;

import cn.zswltech.mithras.customer.domain.constant.LackDataMsg;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.CorpCommerceInfo;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * 校验
 *
 * @author wangchuanhao
 * @date 2022/6/23 3:10 PM
 */
public class CorpCommerceInfoValidator {

    public static void validate(CorpCommerceInfo data) {
        CustomerValidatorSupport.errLackData(Objects.isNull(data.getTripleCertInOne()), LackDataMsg.COMMERCE_TRIPLE_CERT_IN_ONE);
        CustomerValidatorSupport.errLackData(StringUtils.isBlank(data.getOrgCode()), LackDataMsg.COMMERCE_ORG_CODE);
        CustomerValidatorSupport.errLackData(StringUtils.isBlank(data.getContinuousStatus()), LackDataMsg.COMMERCE_CONTINUOUS_STATUS);
        CustomerValidatorSupport.errLackData(Objects.isNull(data.getEstablishDate()), LackDataMsg.COMMERCE_ESTABLISH_DATE);
        CustomerValidatorSupport.errLackData(Objects.isNull(data.getApprovalDate()), LackDataMsg.COMMERCE_APPROVAL_DATE);
        CustomerValidatorSupport.errLackData(Objects.isNull(data.getBizLicenceLongTerm()), LackDataMsg.COMMERCE_BIZ_LICENCE_LONG_TERM);
        CustomerValidatorSupport.errLackData(Objects.isNull(data.getBizLicenseEndDate()), LackDataMsg.COMMERCE_BIZ_LICENSE_END_DATE);
        CustomerValidatorSupport.errLackData(StringUtils.isBlank(data.getIndustryType()), LackDataMsg.COMMERCE_INDUSTRY_TYPE);
        CustomerValidatorSupport.errLackData(StringUtils.isBlank(data.getEconomyType()), LackDataMsg.COMMERCE_ECONOMY_TYPE);
        CustomerValidatorSupport.errLackData(StringUtils.isBlank(data.getOrgType()), LackDataMsg.COMMERCE_ORG_TYPE);
        CustomerValidatorSupport.errLackData(StringUtils.isBlank(data.getOrgScale()), LackDataMsg.COMMERCE_ORG_SCALE);
        CustomerValidatorSupport.errLackData(StringUtils.isBlank(data.getRegisterCurrencyType()), LackDataMsg.COMMERCE_REGISTER_CURRENCY_TYPE);
        CustomerValidatorSupport.errLackData(Objects.isNull(data.getRegisterCapital()), LackDataMsg.COMMERCE_REGISTER_CAPITAL);
        CustomerValidatorSupport.errLackData(Objects.isNull(data.getRealCapital()), LackDataMsg.COMMERCE_REAL_CAPITAL);
        CustomerValidatorSupport.errLackData(Objects.isNull(data.getRegisterCapitalRate()), LackDataMsg.COMMERCE_REGISTER_CAPITAL_RATE);
        CustomerValidatorSupport.errLackData(StringUtils.isBlank(data.getBizScope()), LackDataMsg.COMMERCE_BIZ_SCOPE);
        CustomerValidatorSupport.errLackData(StringUtils.isBlank(data.getCorpRepresent()), LackDataMsg.COMMERCE_CORP_REPRESENT);
        CustomerValidatorSupport.errLackData(StringUtils.isBlank(data.getCorpGender()), LackDataMsg.COMMERCE_CORP_GENDER);
        CustomerValidatorSupport.errLackData(StringUtils.isBlank(data.getCorpCertType()), LackDataMsg.COMMERCE_CORP_CERT_TYPE);
        CustomerValidatorSupport.errLackData(StringUtils.isBlank(data.getCorpCertCode()), LackDataMsg.COMMERCE_CORP_CERT_CODE);
        CustomerValidatorSupport.errLackData(Objects.isNull(data.getListedCompany()), LackDataMsg.COMMERCE_LISTED_COMPANY);
    }

}
