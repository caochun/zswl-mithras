package cn.zswltech.mithras.leaseholdproperty.model;

import cn.zswltech.mithras.foundation.persistence.model.BaseModelWithLogicDelete;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDate;

/**
 * 租赁物增值税发票识别信息
 *
 * @author yupengfei
 * @date 2024/5/8 16:43
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "lease_item_vat_invoice")
public class LeaseItemVatInvoice extends BaseModelWithLogicDelete {
    @TableId(type = IdType.AUTO)
    @TableField("id")
    private Long id;

    /**
     * 租赁物审核id
     */
    @TableField("lease_item_info_id")
    private Long leaseItemInfoId;

    /**
     * 文件id
     */
    @TableField(value = "file_id")
    private Long fileId;
    /**
     * 文件名称
     */
    @TableField(value = "file_name")
    private String fileName;

    /**
     * 发票号码
     */
    @TableField(value = "invoice_no")
    private String invoiceNo;

    /**
     * 发票代码
     */
    @TableField(value = "invoice_daima")
    private String invoiceDaima;

    /**
     * 发票验证码
     */
    @TableField(value = "invoice_correct_code")
    private String invoiceCorrectCode;

    /**
     * 发票类型
     */
    @TableField(value = "invoice_type")
    private String invoiceType;

    /**
     * 不含税总金额
     */
    @TableField(value = "invoice_tax_total")
    private String invoiceTaxTotal;

    /**
     * 含税总金额
     */
    @TableField(value = "invoice_total_cover_tax_digits")
    private String invoiceTotalCoverTaxDigits;

    /**
     * 开票日期
     */
    @TableField("invoice_issue_date")
    private LocalDate invoiceIssueDate;

    /**
     * 购买方
     */
    @TableField("invoice_payer_name")
    private String invoicePayerName;

    /**
     * 销售方
     */
    @TableField("invoice_seller_name")
    private String invoiceSellerName;

    /**
     * 是否盖章
     */
    @TableField("exist_stample")
    private String existStample;

    /**
     * 验真结果
     */
    @TableField("verify_result")
    private String verifyResult;

    /**
     * 发票状态
     */
    @TableField("status")
    private String status;

    /**
     * 备注
     */
    @TableField("note")
    private String note;

    /**
     * 操作
     */
    @TableField("operation")
    private String operation;

    /**
     * 锁定内容不支持修改
     */
    @TableField("locked")
    private String locked;

    public LeaseItemVatInvoice(String fileName){
        this.setFileName(fileName);
    }

}
