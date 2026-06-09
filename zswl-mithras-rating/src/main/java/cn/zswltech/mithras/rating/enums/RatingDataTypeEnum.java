package cn.zswltech.mithras.rating.enums;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RatingDataTypeEnum implements PullDown {

    NUMBER("数值","number",0),
    DATE("日期","date",1),
    TIME("时间","time",2),
    STRING("字符串","string",3),
    COLLECTION("集合","collection",4),
    ENUM("枚举","enum",5),
    ENUM_COLLECTION("枚举集合","enum_collection",6),
    ;

    public final String display;
    public final String extra;
    public final int sort;

    @Override
    public String display() {
        return display;
    }


    public static RatingDataTypeEnum find(String name) {
        for (RatingDataTypeEnum item : values()) {
            if (item.name().equals(name)) {
                return item;
            }
        }
        return null;
    }

    public static RatingDataTypeEnum findByExtra(String extra) {
        for (RatingDataTypeEnum item : values()) {
            if (item.getExtra().equals(extra)) {
                return item;
            }
        }
        return null;
    }


}
