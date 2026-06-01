package cn.zswltech.mithras.service.enums.contract;

public enum ContractModelEnum {
    CONSULT("咨询"), GUARANTEE("保证"), MORTGAGE("抵"), PLEDGE("质");

    ContractModelEnum(String display) {
        this.display = display;
    }

    public final String display;

    public static ContractModelEnum of(String code) {
        for (ContractModelEnum value : ContractModelEnum.values()) {
            if (value.name().equals(code)) {
                return value;
            }
        }
        return null;
    }
}
