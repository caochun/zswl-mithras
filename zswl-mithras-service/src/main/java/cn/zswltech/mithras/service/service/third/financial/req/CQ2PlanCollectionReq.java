package cn.zswltech.mithras.service.service.third.financial.req;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

/**
 * 苍穹2期 通用应收单_新增
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class CQ2PlanCollectionReq extends CQ2CommonReq {

    private String requestId;//非必传参数，防止接口被重复调用 y
    private String cico_srcsystem;// RZY
    private String cico_srcbillno;
    private String bizdate;//单据日期-最后一笔实收日期 y
    private String cico_acctagebegining;//同单据日期
    private String asstacttype;//往来类型 [bd_customer:客户,bd_supplier:供应商,bos_user:人员].1. 厂商质保金，默认“bd_supplier”其他都默认“bd_customer”y
    private Boolean cico_ishavecontr;//是否有合同 n
    private String remark;//说明 n
    private Boolean cico_morecurrency;//多币别 n
    private BigDecimal exchangerate;//汇率 默认“1” y
    private String exratedate;//汇率日期 n
    private BigDecimal recamount;//应收金额 n
    private BigDecimal amount;//不含税金额 n
    private BigDecimal tax;//税额 n
    private Boolean cico_isinvoice;//是否开票
    private String cico_invoiceemail;//电子发票收票邮箱 n
    private String cico_invoicephone;//电子发票收票电话 n
    private String creator_number;//创建人.工号 y
    private String org_number;//结算组织.编码 默认“10000396” y
    private String department_number;//部门.编码 业务部门 y
    private String asstact_number;//客户.编码 客户编码 y
    //private String cico_contractnum_number;//合同号.合同编码 y
    private String recorg_number;//收款组织.编码 默认“10000396” y
    private String cico_applyorg_number;//申请开票组织.编码 默认“10000396” y
    private String currency_number;//结算币别.货币代码 默认”RMB" y
    private String cico_invoicetype_number;//发票类型.编码 n
    private String cico_comment;//取合同的核算规则：剩余本金法 or 实际利率法 y
    /**
     * 来源系统 [FMIS:财务共享系统,
     *  REDS:营收数据系统,
     * XRXT:西软系统,
     * LCXT:路产赔付系统,
     * ICKXT:ic卡系统,
     * SGPT:商管系统,
     * RZY:融租易系统,
     * FJXT:国大商管系统,
     *  XSCXT:新视窗系统,
     *  HYJY:航运经营系统]
     **/
    private String cico_paynum_rby;//交易流水号 银行流水ID y.需等流水中心改造后获取
    private String billtype_number;//单据类型

    private List<CQCollectionBody> entry;

    @Data
    public class CQCollectionBody{
        private Long id;//明细.id n
        private String cico_zb_material;//明细.商品名称 n
        private String e_spectype;//明细.规格型号 n
        private BigDecimal e_quantity;//明细.数量 默认“1” y
        private BigDecimal e_taxrate;//明细.税率(%) 根据业务类型提供,直租："13"，其他业务类型"6".因为该字段单位为%，所以接口提供的数值不用带%。 y
        /**
         * 业务系统计算：
         * 根据现金流项目判断：
         * 1）现金流项目=本金/客户保证金/厂商质保金/首期租金，则 不含税单价= 实收金额；
         * 2）现金流项目=else，则 不含税单价= 实收金额/（1+税率）
         **/
        private BigDecimal e_unitprice;//明细.不含税单价（元）y

        private BigDecimal e_taxunitprice;//明细.含税单价（元）实收金额，单位元 y
        /**
         * 业务系统计算：
         * 根据现金流项目判断：
         * 1）现金流项目=本金/客户保证金/厂商质保金/首期租金，则 税额=0
         * 2）现金流项目=else，则 税额= 不含税单价* 税率
         **/
        private BigDecimal e_tax;//明细.税额（元）y
        private BigDecimal e_recamount;//明细.价税合计 实收金额，单位元 y
        private BigDecimal e_reclocalamt;//明细.应收金额(本位币) n
        private BigDecimal e_localamt;//明细.金额(本位币) n
        private BigDecimal e_taxlocalamt;//明细.税额(本位币) n
        private String e_remark;//明细.备注取合同的核算规则：剩余本金法 or 实际利率法 y
        private String cico_incomeitems_number;//收入项目.编码 根据现金流项目，与“收入项目”建立 mapping 关系，见sheet：应收单-收入项目 y
        private String e_material_number;//开票名称.编码 n
        private String cico_taxclassification_number;//税收分类编码.编码 n
        private String cico_e_businesstype_number;//业务类型.编码 y
        private String project_name;//项目.项目编码 合同编号 y
        private String docVersion;
        private String docInventoryQty;

    }

}