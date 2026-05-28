package cn.zswltech.mithras.service.service.third.financial.req;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 苍穹2期 收款单
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class CQ2CollectionReq extends CQ2CommonReq {
    private Long id;
    private String cico_srcbillno;
    private String billstatus;
    private String bizdate;//业务日期
    /**
     * "付款人类型
     * [bd_customer:客户,
     * bd_supplier:供应商,
     * bos_org:公司,
     * bos_user:职员,
     * other:其他]"
     **/
    private String payertype;//"1. 厂商质保金，默认“bd_supplier” 2. 除了厂商质保金之外，其他都默认“bd_customer”"
    //private String itempayertype;//同 payertype
    private String paymentmode;
    private Boolean cicoIsintrabranch;
    private Boolean cicoIsasstact;
    // private String payeedate;//收款日期 实收日期
    private Boolean cicoIshavecontr;
    private String receivingtype_number;//收款类型.编码 根据业务场景，与“收款类型”建立 mapping 关系，见 sheet：收款单-收款类型&资金用途
    private String actpayaccountNumber;
    private String cicoBilltypeidNumber;
    private String cicoOrgNumber;
    private String cicoRelateddepartmentsNumber;
    private String cicoContractnumNumber;
    private String cicoSrcbillno;
    private String cicoRentactualcode;
    /**
     * "来源系统 [FMIS:财务共
     * 享系统, MYXT:交控明源
     * 系统, RZXT:保融资金系
     * 统, XSCXT:新视窗系统,
     * DZZBXT:电子招标系统,
     * ZHGLXT:综合管理系统]"
     **/
    private String cico_srcsystem;// 默认“RZY”
    private String cicoCustomerNumber;
    private String cicoFinancialproductsNumber;
    private String f7PayerNumber;
    private String customerf7Number;
    private String userf7Number;
    private String payeeCompanyNumber;
    private String f7PayerbankNumber;
    private Date exratedate;
    private BigDecimal exchangerate;
    private String quotation;
    private String settletnumber;
    private List<CQ2CollectionReqBody> entry;//收款明细
    private BigDecimal actrecamt;//收款金额 实收金额
    private BigDecimal localamt;
    private String openorg;
    private String org_number;
    private BigDecimal fee;
    private Boolean cicoIspush;
    private String currencyNumber;
    private String exratetableNumber;
    private String settletypeNumber;
    private boolean cico_ishavecontr;//是否合同收付
    private String sourcebillnumber;//源单编码 银行流水的 交易明细编号
    private String cico_billtypeid_number;//单据类型.编码 默认cas_recbill_BT
    private String payername;
    private String payernumber;
    private String creator_number;//经办人
    private String accountbank_number;//银行账号
    //private String itempayer_number;
    //private String payee_company_number;//付款方
    private String txt_description;//摘要1 收到xxx客户现金流
    private String currency_number;// 币别
    private String exratetable_number;//汇率表.编码
    private String settletype_number;//结算方式

    private String cico_relateddepartments_number;//关联部门


    @Data
    public class CQ2CollectionReqBody {
        private Long id;
        private String cicoSubjectNumber;
        private String cicoEntrytype;
        private BigDecimal e_discountamt;//收款明细.现金折扣
        private BigDecimal e_receivableamt;//收款明细.应收金额 实收金额
        private BigDecimal e_receivablelocamt;//应收则本币 = 应收金额
        private BigDecimal e_actamt;//收款明细.实收金额 实收金额
        private String eRemark;
        private BigDecimal eFee;
        private String eClaimbill;
        private String contractnumber;
        private Date entrybizdate;
        private String conbillnumber;
        private BigDecimal cicoInterestamt;
        private Boolean cicoIfcollection;
        private String realreccompanyNumber;
        private String e_settleorg_number;//结算组织.编码 默认“10000396”
        private String cicoTxfdsorgNumber;
        private String cicoBusinesstypeNumber;
        private String cicoReceiptitemNumber;
        private String cicoBoatnameNumber;
        private String cico_customerfield_number;//客户.编码
        private String cicoTrainingTypeNumber;
        private String costcenterNumber;
        private String cicoVoyagenumberNumber;
        private String eExpenseitemNumber;
        private String projectNumber;
        private Boolean cicoCheckrece;
        private String e_fundflowitem_number;//资金用途.编码 根据业务场景，与“资金用途”建立 mapping 关系，见 sheet：收款单-收款类型&资金用途
        //private String cico_purposeoffunds_number;//国资委资金用途.编码 根据业务场景，与“国资委资金用途”建立 mapping 关系，见 sheet：收款单-收款类型&资金用途
        private String cicoResponsibilityNumber;
        private String productlineNumber;
        private String bizunitNumber;
        private String cicoIntrabranchorgNumber;
        private String cicoYwxmNumber;
        private String cicoDeptNumber;
        private String cicoPersonNumber;
        private String cicoBasedatafieldNumber;
        private String cicoEHouseztNumber;
        private String cicoEHousenoNumber;
        private String cicoFwytNumber;
        private String cicoTypeserviceNumber;
    }
}