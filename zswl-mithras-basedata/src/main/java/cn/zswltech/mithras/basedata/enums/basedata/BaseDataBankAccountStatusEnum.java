package cn.zswltech.mithras.basedata.enums;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author dingqi
 * @date 2023/2/22
 * @description
 */
@AllArgsConstructor
@Getter
public enum BaseDataBankAccountStatusEnum implements PullDown {
    OPENING("开户"),
    NORMAL("正常使用"),
    OVERDRAFT("透支"),
    DORMANCY("休眠"),
    FREEZE("冻结"),
    CANCEL("注销");

    private final String display;

    @Override
    public String display() {
        return this.display;
    }
}
