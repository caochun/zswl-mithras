package cn.zswltech.mithras.third.service.financial.vo;

import cn.zswltech.mithras.third.service.financial.req.CQ2CommonReq;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;


/**
 * 苍穹2期 - 付款申请单
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class CQ2PaymentVO extends CQ2CommonReq {
    private String cico_srcbillno;//来源流水号  Y
    private String settleorg_number;//结算组织.编码 默认“10000396”
    private String applyorg_number; //申请组织.编码 默认“10000396”
    private String cico_dept_number;//部门.编码
    private String payorg_number;//付款组织.编码  Y  默认“10000396”
    private String cico_payzh_number;//付款人银行账号.编码 项目端.付款核销结算明细中的 银行账号；
    //private String payzh_bank_name;//开户行.名称 项目端.付款核销结算明细中的 开户行；
    private String applydate;//申请日期 Y 该笔付款申请下第一笔投放的日期
    private String creator_number;//经办人.工号 Y默认 获取 token 的账号
    //private BigDecimal exchangerate;//汇率 默认“1”
    //private String paycurrency_number;//币别.货币代码 默认”RMB"
    //private String settlecurrency_number;//结算币别.货币代码 默认”RMB"
    private boolean cico_ishavecontr;//是否有合同 Y  现有推送均为false
    private String billtype_number;//单据类型 Y 默认“ap_payapply_BT_zb”，付款申请单（总部）
    /**
     * 按以下格式拼接：考核部门：主承租人名称，业务类型
     * 示例：高端业务部：华西钢铁，售后回租
     * 其中：部门，目前只能取到一个部门，没有考核部门，暂是先取合同所属业务部门；
     * 业务类型：如果是租赁业务，就取租赁业务。其他取业务类型"
     * 融资端：归还【融资渠道】本息
     **/
    private String applycause;//情况说明
    private String cico_srcsystem;//来源系统  Y 默认 RZY:融租易系统
    private List<CQ2PaymentVOEntry> entry;//明细
    private String cico_paynum_rby;//交易流水号 银行流水ID y.需等流水中心改造后获取

    @Data
    public class CQ2PaymentVOEntry{
        private String e_paymenttype_number;//付款类型.编码 根据现金流项目，与“付款类型”建立 mapping 关系，见sheet：付款申请单-付款类型
        /**
         * [bd_supplier:供应商,
         * bos_user:人员,
         * bos_org:公司,
         * bd_customer:客户,
         * cico_other:其他]
         **/
        private String e_asstacttype;//明细.收款方类型 Y 默认“bd_customer” 银行机构待确认
        private BigDecimal e_applyamount;//实付金额 Y
        private String e_asstact_name;//收款单位名称.名称 Y 项目端：客户名称 融资端：金融机构名称
        private String cico_pay_bank_number_number;//付款人银行账号.编码 我方银行账号
        //private String cico_pay_bank_name_number;//
        private String cico_pay_bank_name_name;//付款人开户银行.名称 我方开户行
        /**
         * 项目端-投放款：取付款核销中‘付款方式’
         * 项目端-退保证金：默认“网银”
         * 融资端-还本付息：默认“网银”
         **/
        private String e_settlementtype_number;//支付方式.编码 CQPaymentMethodENUM   Y
        private String cico_e_assacct;//明细.收款人银行账号
        private String cico_e_asstact;//
       /* private String cico_accountname;//明细.收款人账户名称
        private String e_bebank_number;//"收款人开户银行.名称 收款人开户银行.编码"
        private BigDecimal e_appseleamount;//"明细.申请金额折结算币别"
        private BigDecimal e_approvedamt;//明细.核准金额*/
        private String cico_settemenorg_number;//结算组织.编码 Y
        private String e_asstact;//"项目端：客户的客户编码 融资端：默认“10000396”"
        private String cico_uniquecode;//交易流水号 银行流水ID
        private String cico_businesstype_number;//业务类型.编码

    }

}
