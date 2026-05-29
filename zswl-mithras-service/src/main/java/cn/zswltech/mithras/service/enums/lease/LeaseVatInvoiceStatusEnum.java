package cn.zswltech.mithras.service.enums.lease;

import cn.zswltech.mithras.common.enums.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * 发票状态
 *
 * @author yupengfei
 * @date 2024/5/11 17:43
 */
@Getter
@AllArgsConstructor
public enum LeaseVatInvoiceStatusEnum implements PullDown {

    EFFECTIVE("N", "有效"),
    CANCELLATION("Y", "已作废"),
    CREDIT_IN_RED("H", "冲红"),
    PARTIAL_OFFSET_IN_RED("7", "部分冲红"),
    FULL_OFFSET_IN_RED("8", "全额冲红"),
    ;

    public final String fieldName;
    public final String display;

    public static LeaseVatInvoiceStatusEnum ofByFieldName(String fieldName) {
        for (LeaseVatInvoiceStatusEnum item : values()) {
            if (Objects.equals(item.getFieldName(), fieldName)) {
                return item;
            }
        }
        return null;
    }

    public static LeaseVatInvoiceStatusEnum ofByName(String name) {
        for (LeaseVatInvoiceStatusEnum item : values()) {
            if (Objects.equals(item.name(), name)) {
                return item;
            }
        }
        return null;

    }

    @Override
    public String display() {
        return display;
    }
}
