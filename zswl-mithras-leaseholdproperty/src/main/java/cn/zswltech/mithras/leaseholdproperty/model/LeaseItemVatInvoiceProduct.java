package cn.zswltech.mithras.leaseholdproperty.model;

import cn.zswltech.mithras.foundation.persistence.model.BaseModelWithLogicDelete;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * 租赁物增值税发票产品信息
 *
 * @author yupengfei
 * @date 2024/5/8 19:24
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "lease_item_vat_invoice_product")
public class LeaseItemVatInvoiceProduct extends BaseModelWithLogicDelete {
    @TableId(type = IdType.AUTO)
    @TableField("id")
    private Long id;

    /**
     * 发票id
     */
    @TableField("invoice_id")
    private Long invoiceId;

    /**
     * 货物或服务名称
     */
    @TableField("invoice_goods")
    private String invoiceGoods;

    /**
     * 规格型号
     */
    @TableField("invoice_plate_specific")
    private String invoicePlateSpecific;

    /**
     * 单位
     */
    @TableField("invoice_electrans_unit")
    private String invoiceElectransUnit;

    /**
     * 数量
     */
    @TableField("invoice_electrans_quantity")
    private String invoiceElectransQuantity;

    /**
     * 税率
     */
    @TableField("invoice_tax_rate")
    private String invoiceTaxRate;

    /**
     * 税额
     */
    @TableField("invoice_tax")
    private String invoiceTax;

    /**
     * 金额（含税）
     */
    @TableField("invoice_price")
    private String invoicePrice;

    /**
     * 金额(不含税)
     */
    @TableField("tax_not_included")
    private String taxNotIncluded;

    /**
     * 车辆识别代号/车架号码 （车辆发票使用）
     */
    @TableField("vehicle_invoice_car_vin")
    private String vehicleInvoiceCarVin;
}
