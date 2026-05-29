package cn.zswltech.mithras.service.enums.contract.text;

import cn.zswltech.mithras.common.enums.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author bigbear
 * @date 2024/12/3 09:14
 * @description 合同签约状态枚举
 */
@Getter
@AllArgsConstructor
public enum ContractSignStatusEnum implements PullDown {
    DRAFT("草稿"),
    SIGNING("签署中"),
    COMPLETE("已完成"),
    REJECTED("已退回"),
    RECALLED("已撤回"),
    EXPIRED("已过期"),
    FILLING("拟定中"),
    TERMINATING("作废确认中"),
    TERMINATED("已作废"),
    DELETE("已删除"),
    FINISHED("已完成");

    private final String display;


    @Override
    public String display() {
        return display;
    }

    public static ContractSignStatusEnum ofName(String name) {
        for (ContractSignStatusEnum item : ContractSignStatusEnum.values()) {
            if (item.name().equals(name)) {
                return item;
            }
        }
        return null;
    }
}
