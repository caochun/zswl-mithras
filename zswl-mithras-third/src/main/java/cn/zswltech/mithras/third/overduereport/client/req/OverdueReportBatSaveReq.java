package cn.zswltech.mithras.third.overduereport.client.req;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 应收逾期集成保存接口
 * @author: jackerhe
 **/
@Data
public class OverdueReportBatSaveReq {

    /** 合同编码.合同编码 (必填) */
    private String cico_contract_number;

    /** 核算组织.编码 (必填) */
    private String cico_account_org_number;

    /** 客商编码.编码 (必填) */
    private String cico_customer_number;

    /** 款项内容.款项内容编码 (必填) */
    private String cico_payment_number;

    /** 科目.编码 (必填) */
    private String cico_accounttype_number;

    /** 原币币别.货币代码 (必填) */
    private String cico_origin_currency_number;

    /** 本位币币别.货币代码 (必填) */
    private String cico_functional_currency_number;

    /** 经办人.工号 (必填) */
    private String cico_handler_number;


    /** 单据修改时间 (必填) "2025-07-28 16:44:34" */
    private String modifytime;

    /** 源单类型 */
    private String cico_origin_type;

    /** 源单编码 (必填) */
    private String cico_origin_no;

    /**
     * 数据插入方式 [1:接口, 2:引入] 默认接口
     */
    private String cico_insertdata_type;

    /**
     * 客户类型 [bd_customer:客户, bd_supplier:供应商, bos_user:人员] (必填)
     */
    private String cico_customer_type;

    /** 单据账龄起算日  "2025-07-28"*/
    private String cico_start_date;

    /** 单据日期 (必填) "2025-07-28"*/
    private String cico_bill_date;

    /** 约定收款日期 (必填) "2025-07-28"*/
    private String cico_due_date;

    /** 约定收款条件 (必填) */
    private String cico_payment_terms;

    /** 应收金额 (原币) (必填) */
    private BigDecimal cico_rece_amount;

    /** 应收金额 (本位币) (必填) */
    private BigDecimal cico_rece_amount_f;

    /** 行业正常收款周期 (必填) */
    private Integer cico_collection_cycle;

    /** 已结算金额（原币） */
    private BigDecimal cico_settled_amount;

    /** 已结算金额(本位币) */
    private BigDecimal cico_settled_amount_f;

    /** 未结算金额 (原币) */
    private BigDecimal cico_unsettled_amount;

    /** 未结算金额 (本位币) */
    private BigDecimal cico_unsettled_amt_f;

    /** 客户地域编码 */
    private String cico_customer_region_number;

    /**
     * 国有类型 [
     *  1:非国有,
     *  2:中央政府及国家机关,
     *  3:地方政府及国家机关,
     *  4:央企,
     *  5:地方国企,
     *  6:事业单位
     * ]
     */
    private String cico_state_type;

    /** 实控人 */
    private String cico_actual_controller;

}