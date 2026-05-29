package cn.zswltech.mithras.factory.enums;

import cn.zswltech.mithras.common.enums.PullDown;
import cn.zswltech.mithras.service.others.MithrasException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum RatingLevelEnum implements PullDown {


    AAA("AAA",14),
    AA_UP("AA+",13),
    AA("AA",12),
    AA_DOWN("AA-",11),
    A_UP("A+",10),
    A("A",9),
    A_DOWN("A-",8),
    BBB("BBB",7),
    BB("BB",6),
    B("B",5),
    CCC("CCC",4),
    CC("CC",3),
    C("C",2),
    D("D",1)
    ;

    public final String display;
    public final int score;

    @Override
    public String display() {
        return display;
    }

    public static RatingLevelEnum findByDisplay(String display) {
        for (RatingLevelEnum item : values()) {
            if (item.getDisplay().equals(display)) {
                return item;
            }
        }
        throw new MithrasException("该评分未注册" + display );
    }

    public static RatingLevelEnum find(String name) {
        for (RatingLevelEnum item : values()) {
            if (item.name().equals(name)) {
                return item;
            }
        }
        throw new MithrasException("该评分未注册" + name );
    }
}
