package cn.zswltech.mithras.service.enums.capital.third;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author yangxiong
 * @date 2024/5/23/17:22
 * @description
 */
@Getter
@AllArgsConstructor
public enum DataSourceEnum {
    FROM_BANK("frombank", "银企接口"),
    IMPORT("import", "模板引入"),
    RECEIPT_GEN("receiptgen", "电子回单生成"),
    FROM_IFM("fromifm", "内部金融"),
    ;

    private final String code;
    private final String display;

    public static DataSourceEnum of(String code) {
        for (DataSourceEnum dataSourceEnum : DataSourceEnum.values()) {
            if (dataSourceEnum.code.equals(code)) {
                return dataSourceEnum;
            }
        }
        return null;
    }

}
