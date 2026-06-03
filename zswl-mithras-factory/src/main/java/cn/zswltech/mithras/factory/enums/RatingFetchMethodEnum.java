package cn.zswltech.mithras.factory.enums;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RatingFetchMethodEnum implements PullDown {

    SYSTEM("系统取数","system",0),
    IMPORT("手工录入","import",1)
    ;

    public final String display;
    public final String extra;
    public final int sort;

    @Override
    public String display() {
        return display;
    }


    public static RatingFetchMethodEnum find(String name) {
        for (RatingFetchMethodEnum item : values()) {
            if (item.name().equals(name)) {
                return item;
            }
        }
        return null;
    }
}
