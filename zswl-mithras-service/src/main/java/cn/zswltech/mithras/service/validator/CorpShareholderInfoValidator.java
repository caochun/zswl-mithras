package cn.zswltech.mithras.service.validator;

import cn.zswltech.mithras.service.constant.LackDataMsg;
import cn.zswltech.mithras.service.mapper.model.client.CorpShareholderInfo;
import cn.zswltech.mithras.service.others.Util;
import org.apache.commons.lang3.StringUtils;

/**
 * 校验
 *
 * @author wangchuanhao
 * @date 2022/6/23 3:10 PM
 */
public class CorpShareholderInfoValidator {

    public static void validate(CorpShareholderInfo data) {
        Util.errLackData(StringUtils.isBlank(data.getShareholderType()), LackDataMsg.SHAREHOLDER_TYPE);
        Util.errLackData(StringUtils.isBlank(data.getShareholderName()), LackDataMsg.SHAREHOLDER_NAME);
    }

}
