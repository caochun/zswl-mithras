package cn.zswltech.mithras.rating.enums;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RatingModelTypeEnum implements PullDown {

    PROJ("项目制",0),
    POLITICE_CREDIT("政信类",1),
    NORMAL("通用类",2)
    ;

    public final String display;
    public final int sort;

    @Override
    public String display() {
        return display;
    }


    public static RatingModelTypeEnum find(String name) {
        for (RatingModelTypeEnum item : values()) {
            if (item.name().equals(name)) {
                return item;
            }
        }
        return null;
    }
}
