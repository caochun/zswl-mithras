package cn.zswltech.mithras.creditreport.enums;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@AllArgsConstructor
@Getter
public enum CreditSearchStatusEnum implements PullDown {

    SUCCESS("成功","SUCCESS"),
    SEARCHING("查询中", "SEARCHING"),
    FAIL("失败","FAIL"),
    ;

    private final String display;
    /**
     * 查询状态
     */
    private final String name;

    @Override
    public String display() {
        return this.display;
    }

    public static CreditSearchStatusEnum finaByName(String name) {
        for (CreditSearchStatusEnum item : values()) {
            if (Objects.equals(item.name(), name)) {
                return item;
            }
        }
        return null;
    }
}