package cn.zswltech.mithras.finance.mapper.model.finance;

import cn.zswltech.mithras.finance.enums.third.FinancialAccountAgeSendStatusStatus;
import cn.zswltech.mithras.finance.enums.third.FinancialAccountNumberENUM;
import cn.zswltech.mithras.finance.enums.third.FinancialPaymentContentENUM;
import cn.zswltech.mithras.service.mapper.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @description 帐龄-详情表
 * @author vico
 * @date 2024-09-10
 */
@Data
public class FinanceAccountAgeItem extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * 主键id
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
    * 帐龄id
    */
    @TableField("account_age_id")
    private Long accountAgeId;

    /**
    * 核算组织编码 默认 10000396
    */
    @TableField("accountancy_organization_number")
    private String accountancyOrganizationNumber;

    /**
    * 核算组织名称 默认 浙江浙商融资租赁有限公司
    */
    @TableField("accountancy_organization_name")
    private String accountancyOrganizationName;

    /**
    * 状态 null 未推送，0推送成功 ,1 推送失败
     * {@link FinancialAccountAgeSendStatusStatus#name}
    */
    @TableField("send_status")
    private String sendStatus;

    /**
    * 期初款项原值
    */
    @TableField("original_value_initial")
    private BigDecimal originalValueInitial;

    /**
    * 本期增加额
    */
    @TableField("original_value_increase")
    private BigDecimal originalValueIncrease;

    /**
     * 本期减少额
     */
    @TableField("original_value_reduce")
    private BigDecimal originalValueReduce;

    /**
    * 期末款项原值
    */
    @TableField("original_value_final")
    private BigDecimal originalValueFinal;

    /**
    * 币别
    */
    @TableField("currency")
    private String currency;

    /**
    * 科目名称编号
     * {@link FinancialAccountNumberENUM#name()}
    */
    @TableField("account_number")
    private String accountNumber;

    /**
    * 款项内容
     * {@link FinancialPaymentContentENUM#name()}
    */
    @TableField("payment_content")
    private String paymentContent;

    /**
    * 客户id
    */
    @TableField("client_id")
    private Long clientId;

    /**
    * 客户单位名称， 取合同对应承租人的“客户名称”字段
    */
    @TableField("customer_unit_name")
    private String customerUnitName;

    /**
    * 业务日期
    */
    @TableField("business_date")
    private LocalDate businessDate;

    /**
    * 账龄截止日
    */
    @TableField("aging_deadline")
    private LocalDate agingDeadline;

    /**
    * 业务账龄（月）向下取整
    */
    @TableField("business_age")
    private Integer businessAge;

    /**
    * 合同id
    */
    @TableField("contract_id")
    private Long contractId;

    /**
     *借据ID
     **/
    @TableField("receipt_id")
    private Long receiptId;

    /**
    * 收款id
    */
    @TableField("collection_id")
    private Long collectionId;

    @TableField("collection_code")
    private String collectionCode;

    /**
    * 合同编号
    */
    @TableField("contract_code")
    private String contractCode;

    /**
    * 项目名称
    */
    @TableField("proj_name")
    private String projName;

    /**
    * 租金的应收日期 合同逾期日期
    */
    @TableField("plan_collection_date")
    private LocalDate planCollectionDate;
    /**
     * 来源 0系统生成 ,1 人工创建
     **/
    @TableField("source")
    private Integer source;

    /**
    * 逻辑删除，0-未删除，1-已删除
    */
    @TableField("deleted")
    private Integer deleted;

    @TableField("cq_number")
    private String cqNumber;

}
