package cn.zswltech.mithras.service.validator;

import cn.zswltech.mithras.service.constant.LackDataMsg;
import cn.zswltech.mithras.service.mapper.model.client.CorpBankAccount;
import cn.zswltech.mithras.service.others.Util;
import org.apache.commons.lang3.StringUtils;

/**
 * 校验
 *
 * @author wangchuanhao
 * @date 2022/6/23 3:10 PM
 */
public class CorpBankAccountValidator {

    public static void validate(CorpBankAccount data) {
        Util.errLackData(StringUtils.isBlank(data.getAccountBank()), LackDataMsg.BANK_ACCOUNT_BANK);
        Util.errLackData(StringUtils.isBlank(data.getAccountName()), LackDataMsg.BANK_ACCOUNT_NAME);
        Util.errLackData(StringUtils.isBlank(data.getAccountNumber()), LackDataMsg.BANK_ACCOUNT_NUMBER);
    }

}
