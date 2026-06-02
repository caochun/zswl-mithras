package cn.zswltech.mithras.third.service.financial.req;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

/**
 * 苍穹2期 - 付款申请单
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class CQ2PaymentReq extends CQ2CommonReq {
    private String cico_srcbillno;//来源系统单号
    private String settleorg_number;//结算组织.编码 默认“10000396”
    private String applyorg_number;//申请组织.编码 默认“10000396”
    private String cico_dept_number;//部门.编码
    private String payorg_number;//付款组织.编码 默认“10000396”
    private String cico_payzh_number;//付款人银行账号.编码 项目端.付款核销结算明细中的 银行账号；
    private String payzh_bank_name;//开户行.名称 项目端.付款核销结算明细中的 开户行；
    private String payproperty;//款项性质.编码 n
    private String fundflow_h;//资金用途.编码 n
    private String applydate;//申请日期 该笔付款申请下第一笔投放的日期
    private String creator_number;//经办人.工号 默认 获取 token 的账号
    private BigDecimal exchangerate;//汇率 默认“1”
    private String paycurrency_number;//币别.货币代码 默认”RMB"
    private String settlecurrency_number;//结算币别.货币代码 默认”RMB"
    private String billtype_number;//单据类型
    private String applycause;//情况说明
    private boolean cico_ishavecontr;//false
    private String cico_srcsystem;//RZY:融租易系统
    private List<CQ2PaymentExpenseentry> expenseentry;//费用明细
    private List<CQ2PaymentEntry> entry;//明细
    private String cico_paynum_rby;//交易流水号 银行流水ID y.需等流水中心改造后获取

    @Data
    public class CQ2PaymentExpenseentry{
        private Long id;
        private Boolean capitalization;//费用明细.是否资本化
        private String expensedate;//费用明细.费用发生日期
        private String expensecauses;//费用明细.费用产生原因
        private BigDecimal cico_expenseamt;//费用明细.含税金额
        private BigDecimal cico_orientryamount;//费用明细.不含税金额
        private BigDecimal cico_taxrat;//cico_taxrat
        private BigDecimal cico_taxamount;//费用明细.税额
        private BigDecimal cico_proxyamt;//费用明细.代扣代缴个税
        private BigDecimal cico_amt;//费用明细.费用承担金额
        private String cico_remark;//费用明细.备注
        private String cico_issafe1;//费用明细.是否计入安全费用 [1:是, 2:否]
        private String cico_paymenttype_number;//付款类型.编码
        private String cico_expenseitem_number;//付款明细.编码
        private String cico_ywxm_number;//业务项目.编码
        private String cico_project_number;//项目.项目编码
        private String cico_platenum_number;//车牌号.编码
        private String cico_entrycostcompany_number;//费用承担单位.编码
        private String cico_expensedept_number;//费用承担部门.编码
        private String cico_expenseitem1_number;//费用项目.编码
        private String cico_attributeorg_number;//归口部门.编码
        private String cico_f_businesstype_number;//业务类型.编码
    }

    @Data
    public class CQ2PaymentEntry{
        private String e_paymenttype_number;//付款类型.编码 根据现金流项目，与“付款类型”建立 mapping 关系，见sheet：付款申请单-付款类型
        /**
         * [bd_supplier:供应商,
         * bos_user:人员,
         * bos_org:公司,
         * bd_customer:客户,
         * cico_other:其他]
         **/
        private String e_asstacttype;//明细.收款方类型 默认“bd_customer” 银行机构待确认 -》"bd_supplier"
        private BigDecimal e_applyamount;//实付金额
        private String e_asstact_name;//收款单位名称.名称 项目端：客户名称 融资端：金融机构名称
        private String cico_pay_bank_number_number;//付款人银行账号.编码 我方银行账号
        private String cico_pay_bank_name_name;//付款人开户银行.名称 我方开户行
        /**
         * 项目端-投放款：取付款核销中‘付款方式’
         * 项目端-退保证金：默认“网银”
         * 融资端-还本付息：默认“网银”
         **/
        private String e_settlementtype_number;//支付方式.编码
        private String cico_e_assacct;//明细.收款人银行账号
        private String cico_e_asstact;//
        private String cico_accountname;//明细.收款人账户名称
        private String e_bebank_number;//"收款人开户银行.名称 收款人开户银行.编码"
        private BigDecimal e_appseleamount;//"明细.申请金额折结算币别"
        private BigDecimal e_approvedamt;//明细.核准金额
        private String cico_settemenorg_number;//结算组织.编码
        private String e_asstact;//"项目端：客户的客户编码 融资端：默认“10000396”"
        private String cico_uniquecode;//交易流水号 银行流水ID
        private String cico_businesstype_number;//业务类型.编码
    }

}