package cn.zswltech.mithras.service.mapper.model.creditreport;

import cn.zswltech.mithras.service.enums.creditreport.CreditReportDistributionMethodTypeEnum;
import cn.zswltech.mithras.service.enums.creditreport.CreditReportFiveClassificationEnum;
import cn.zswltech.mithras.service.enums.creditreport.CreditReportLastRepaymentTypeEnum;
import cn.zswltech.mithras.service.enums.creditreport.CreditReportRecordBusinessTypeEnum;
import cn.zswltech.mithras.service.mapper.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * @description 征信报告-信贷记录明细表
 * @author vico
 * @date 2025-12-01
 */
@Data
public class CreditReportRecordDetails extends BaseModel implements Serializable {

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
    * 账户编号
    */
    @TableField("account_number")
    private String accountNumber;

    /**
    * 债权机构
    */
    @TableField("creditor_institution")
    private String creditorInstitution;

    /**
    * 业务种类
     * {@link CreditReportRecordBusinessTypeEnum}
    */
    @TableField("business_type")
    private String businessType;

    /**
    * 开立日期
    */
    @TableField("opening_date")
    private String openingDate;

    /**
    * 到期日
    */
    @TableField("expiration_date")
    private String expirationDate;

    /**
    * 币种
    */
    @TableField("currency")
    private String currency;

    /**
    * 借款金额
    */
    @TableField("loan_amount")
    private String loanAmount;

    /**
    * 发放形式
     * {@link CreditReportDistributionMethodTypeEnum}
    */
    @TableField("distribution_method")
    private String distributionMethod;

    /**
    * 担保方式 CreditReportGuaranteeMethodEnum
    */
    @TableField("guarantee_method")
    private String guaranteeMethod;

    /**
    * 余额
    */
    @TableField("balance")
    private String balance;

    /**
    * 五级分类
     * {@link CreditReportFiveClassificationEnum}
    */
    @TableField("five_classification")
    private String fiveClassification;

    /**
    * 逾期总额
    */
    @TableField("total_overdue_amount")
    private String totalOverdueAmount;

    /**
    * 逾期本金
    */
    @TableField("overdue_principal")
    private String overduePrincipal;

    /**
    * 逾期月数
    */
    @TableField("overdue_month")
    private Integer overdueMonth;

    /**
    * 最近一次还款日期
    */
    @TableField("last_repayment_date")
    private String lastRepaymentDate;

    /**
    * 最近一次还款总额
    */
    @TableField("last_repayment_amount")
    private String lastRepaymentAmount;

    /**
    * 最近一次还款形式
     * {@link CreditReportLastRepaymentTypeEnum}
    */
    @TableField("last_repayment_type")
    private String lastRepaymentType;

    /**
    * 特定交易提示
    */
    @TableField("specific_transaction_prompts")
    private String specificTransactionPrompts;

    /**
    * 授信协议编号
    */
    @TableField("credit_agreement_number")
    private String creditAgreementNumber;

    /**
    * 信息报告日期
    */
    @TableField("information_report_date")
    private String informationReportDate;

    /**
    * 逻辑删除，0-未删除，1-已删除
    */
    @TableField("deleted")
    private Integer deleted;

}
