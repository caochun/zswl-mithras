package cn.zswltech.mithras.contract.enums.contract;

public enum LesseeTypeEnum {

    MAIN_LESSSEE("主承租人"), JOINT_LESSEE("联合承租人"),GUARANTOR("担保人"),MORTGAGOR("抵押人"),PLEDGOR("质押人");

    LesseeTypeEnum(String display) {
        this.display = display;
    }

    public final String display;

    public static LesseeTypeEnum of(String code) {
        for (LesseeTypeEnum value : LesseeTypeEnum.values()) {
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
