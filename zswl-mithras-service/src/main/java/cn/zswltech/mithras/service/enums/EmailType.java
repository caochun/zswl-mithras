package cn.zswltech.mithras.service.enums;

import cn.zswltech.mithras.customer.domain.enums.client.ClientType;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description: 邮件枚举类
 * @Author: huangping
 * @Date: 2025/11/26  21:32
 * @Version: 1.0
 */
@Getter
@AllArgsConstructor
public enum EmailType {


    BF_RELEASE_REMIND_EMAIL("BF_RELEASE_REMIND_EMAIL","客户释放前提醒邮件"),
    COLLECTIO_NRENT_NOTICE("COLLECTIO_NRENT_NOTICE","租金逾期催收通知"),
    COLLECTIO_EXPIRE_NOTICE("COLLECTIO_EXPIRE_NOTICE","租金到期通知"),
    FOLLOW_UP_REMIND_EMAIL("FOLLOW_UP_REMIND_EMAIL","项目资料归档待办催办邮件提醒"),
    SUPPLEMENT_FOLLOW_UP_REMIND_EMAIL("SUPPLEMENT_FOLLOW_UP_REMIND_EMAIL","项目资料归档待办逾期邮件提醒"),
    OVERDUE_REMIND_EMAIL("OVERDUE_REMIND_EMAIL","项目资料归档补充催办邮件提醒"),
    SUPPLEMENT_OVERDUE_REMIND_EMAIL("SUPPLEMENT_OVERDUE_REMIND_EMAIL","项目资料归档补充逾期邮件提醒"),
    ;

    private final String type;
    private final String typeName;


    public static ClientType of(String name) {
        for (ClientType value : ClientType.values()) {
            if (value.name().equals(name)) {
                return value;
            }
        }
        return null;
    }

}
