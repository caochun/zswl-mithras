package cn.zswltech.mithras.customer.application.validator;

import cn.zswltech.mithras.customer.constant.LackDataMsg;
import cn.zswltech.mithras.customer.model.client.CorpBankAccount;
import org.apache.commons.lang3.StringUtils;

/**
 * 校验
 *
 * @author wangchuanhao
 * @date 2022/6/23 3:10 PM
 */
public class CorpBankAccountValidator {

    public static void validate(CorpBankAccount data) {
        CustomerValidatorSupport.errLackData(StringUtils.isBlank(data.getAccountBank()), LackDataMsg.BANK_ACCOUNT_BANK);
        CustomerValidatorSupport.errLackData(StringUtils.isBlank(data.getAccountName()), LackDataMsg.BANK_ACCOUNT_NAME);
        CustomerValidatorSupport.errLackData(StringUtils.isBlank(data.getAccountNumber()), LackDataMsg.BANK_ACCOUNT_NUMBER);
    }

}
