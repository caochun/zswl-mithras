package cn.zswltech.mithras.service.mapper.model.creditreport;

import cn.zswltech.mithras.service.enums.creditreport.CreditReportBusinessTypeEnum;
import cn.zswltech.mithras.service.enums.creditreport.CreditReportPaymentModuleEnum;
import cn.zswltech.mithras.service.enums.creditreport.CreditReportQualityClassificationEnum;
import cn.zswltech.mithras.service.mapper.model.BaseModelWithLogicDelete;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @description 征信报告-未结清信贷及授信信息表
 * @author vico
 * @date 2025-11-14
 */
@Data
public class CreditReportUnsettledSummary extends BaseModelWithLogicDelete implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * id
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
    * 查询编号
    */
    @TableField("credit_code")
    private Long creditCode;

    /**
    * 征信报告基本表id
    */
    @TableField("credit_report_id")
    private Long creditReportId;

    /**
     * 征信报告客户表id
     */
    @TableField("credit_report_client_id")
    private Long creditReportClientId;

    /**
    * 款项模块
     * {@link CreditReportBusinessTypeEnum#name()}
    */
    @TableField("payment_module")
    private String paymentModule;

    /**
    * 款项类型-短期-贴现
     * {@link CreditReportPaymentModuleEnum}
    */
    @TableField("payment_type")
    private String paymentType;

    /**
    * 款项分类-正常，关注-不良-合计
     * {@link CreditReportQualityClassificationEnum}
    */
    @TableField("fund_classification")
    private String fundClassification;

    /**
    * 账户数
    */
    @TableField("account_number")
    private Integer accountNumber;

    /**
    * 账户余额
    */
    @TableField("account_amount")
    private BigDecimal accountAmount;

}
