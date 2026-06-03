package cn.zswltech.mithras.leaseholdproperty.domain.enums;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author yupengfei
 * @date 2024/5/13 21:32
 */
@Getter
@AllArgsConstructor
public enum LeaseVatInvoiceTypeEnum implements PullDown {

    VAT_SPECIAL_INVOICE("vat_special_invoice", "增值税专用发票"),
    VAT_ELECTRONIC_INVOICE("vat_electronic_invoice", "增值税电子普通发票"),
    VAT_COMMON_INVOICE ("vat_common_invoice", "增值税普通发票"),
    VAT_ELECTRONIC_SPECIAL_INVOICE("vat_electronic_special_invoice", "增值税电子专用发票"),
    VAT_ELECTRONIC_TOLL_INVOICE("vat_electronic_toll_invoice", "增值税电子普通发票(通行费)"),
    BLOCKCHAIN_ELECTRONIC_INVOICE("blockchain_electronic_invoice", "区块链电子发票"),
    VAT_ELECTRONIC_INVOICE_NEW("vat_electronic_invoice_new", "电子发票(普通发票)"),
    VAT_ELECTRONIC_SPECIAL_INVOICE_NEW("vat_electronic_special_invoice_new", "电子发票(增值税专用发票)"),
    MOTOR_VEHICLE_SALE_INVOICE("motor_vehicle_sale_invoice","机动车销售统一发票"),
    OTHER("other", "其他"),
    ;

    public final String fieldName;
    public final String display;

    @Override
    public String display() {
        return display;
    }
}
