package cn.zswltech.mithras.service.enums.contract;

public enum ResolutionTypeEnum {

    SHAREHOLDERS_RESOLUTION("股东会决议"), DIRECTORS_RESOLUTION("董事会决议")
    , SHAREHOLDER_DECISION("股东决定"), EXECUTE_DIRECTOR_RESOLUTION("执行董事决定"),OTHER("其他");


    ResolutionTypeEnum(String display) {
        this.display = display;
    }

    public final String display;

    public static ResolutionTypeEnum of(String code) {
        for (ResolutionTypeEnum value : ResolutionTypeEnum.values()) {
            if (value.name().equals(code)) {
                return value;
            }
        }
        return null;
    }

    public String getDisplay() {
        return display;
    }
}
