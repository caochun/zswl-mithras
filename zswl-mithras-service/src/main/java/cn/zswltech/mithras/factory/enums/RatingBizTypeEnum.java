package cn.zswltech.mithras.factory.enums;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RatingBizTypeEnum implements PullDown {

    CLIENT("客户评级","client",0),
    PROJ("债项评级","amount",1)
    ;

    public final String display;
    public final String extra;
    public final int sort;

    @Override
    public String display() {
        return display;
    }


    public static RatingBizTypeEnum find(String name) {
        for (RatingBizTypeEnum item : values()) {
            if (item.name().equals(name)) {
                return item;
            }
        }
        return null;
    }
}
