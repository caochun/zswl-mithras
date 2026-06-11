package cn.zswltech.mithras.customer.enums;

/**
 * @author junke
 * 财务信息，报告期
 */
public enum SubjectQuarterType {
    FIRST(1, "1月"),
    SECOND(2, "2月"),
    THIRD(3, "3月(一季报)"),
    FOURTH(4, "4月"),
    FIFTH(5, "5月"),
    SIXTH(6, "6月(半年报)"),
    SEVEN(7, "7月"),
    EIGHTH(8, "8月"),
    NINTH(9, "9月(三季报)"),
    TENTH(10, "10月"),
    ELEVENTH(11, "11月"),
    TWELFTH(12, "12月(年报)");

  /*  FIRST(1, "一季报"), MIDDLE(2, "中报"),

    THIRD(3, "三季报"), YEAR(4, "年报");*/

    SubjectQuarterType(Integer value, String display) {
        this.value = value;
        this.display = display;
    }

    public final String display;
    public final Integer value;

    public static SubjectQuarterType ofDisplay(String display) {
        for (SubjectQuarterType value : SubjectQuarterType.values()) {
            if (value.display.equals(display)) {
                return value;
            }
        }
        return null;
    }

    public static SubjectQuarterType of(String code) {
        for (SubjectQuarterType value : SubjectQuarterType.values()) {
            if (value.name().equals(code)) {
                return value;
            }
        }
        return null;
    }

    public static SubjectQuarterType getByValue(Integer value) {
        for (SubjectQuarterType sqt : SubjectQuarterType.values()) {
            if (sqt.value.equals(value)) {
                return sqt;
            }
        }
        return null;
    }
}
