package cn.zswltech.mithras.third.financialshare.infrastructure.client.req;

import lombok.Data;

import java.util.List;

/**
 * @ClassName ProvisionalVo
 * @Description 苍穹暂收单
 * @Author jackerhe
 * @Date 2022/10/21 5:12 下午
 * @Version 1.0
 **/
@Data
public class ProvisionalREQ {
    //经办人编码
    private String creator;

    //结算组织
    private String org;

    //单据编号
    private String billno;

    //单据类型
    private String billtypeid;

    //单据日期 2022.6.26
    private String bizdate;

    //收款方类型
    private String asstacttype;

    //付款人银行账号
    private String cico_payzh;

    //付款人开户银行
    private String cico_paybank;

    //款项性质
    private String payproperty;

    //合同号
    private String cico_contractnum;

    //来源系统
    private String cico_srcsystem;

    //情况说明
    private String remark;

    //部门
    private String department;

    //流水ID
    private String pknumber;

    private List<ProvisionalAmountinfo> amountinfo;

    private List<ProvisionalDetail> detail;

    private List<ProvisionalPreinfo> preinfo;

    @Data
    public class ProvisionalAmountinfo{

    //收款单位名称
    private String asstact;

    //收款人银行账号
    private String payeebanknum;

    //收款人开户银行
    private String bebank;

    //本期应付金额
    private String pricetaxtotal;

    //本期保证金&暂扣款
    private String premiumamt;

    //币别
    private String currency;

    //多币别
    private String cico_morecurrency;

    //未开票金额
    private String uninvoicedamt;

    }

    @Data
    public class ProvisionalDetail{
        //单据体.预算余额
        private String cico_budgetamount;

        //单据体.费用承担部门
        private String cico_subject;

        //单据体.费用承担单位
        private String cico_responsibleunit;

        //单据体.不含税单价
        private String price;

        //单据体.折扣方式
        private String discountmode;

        //单据体.应付金额
        private String e_pricetaxtotal;

        //单据体.说明
        private String e_remark;

        //单据体.发票代码
        private String cico_entry_invoicecode;

        //单据体.发票号码
        private String cico_entry_invoiceno;
    }

    @Data
    public class ProvisionalPreinfo{

    //支付方式
    private String plansettletype;

    //付款类型
    private String cico_paymenttype;

    //申请支付金额
    private String planpricetax;

    //备注
    private String planremark;

    }


}
