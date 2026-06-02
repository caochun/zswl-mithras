package cn.zswltech.mithras.third.service.financial.vo;

import cn.zswltech.mithras.third.enums.CQCollectionTypeENUM;
import cn.zswltech.mithras.third.service.financial.req.CQ2CommonReq;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;


/**
 * 苍穹2期 收款单 只保留了必填的基本数据
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class CQ2CollectionVO extends CQ2CommonReq {

    private String cico_srcbillno;
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
    //private String payeedate;//收款日期 实收日期
    /**
     * {@link CQCollectionTypeENUM#name()}
     **/
    private String receivingtype_number;//收款类型.编码 根据业务场景，与“收款类型”建立 mapping 关系，见 sheet：收款单-收款类型&资金用途
    /**
     * "来源系统 [FMIS:财务共
     * 享系统, MYXT:交控明源
     * 系统, RZXT:保融资金系
     * 统, XSCXT:新视窗系统,
     * DZZBXT:电子招标系统,
     * ZHGLXT:综合管理系统]"
     **/
    private String cico_srcsystem;// 默认“RZYXT”
    private String payername;//
    private String payernumber;
    private String creator_number;//经办人.工号 默认 获取 token 的账号
    //private String itempayer_number;
    private List<CQ2CollectionVOBody> entry;//收款明细
    private BigDecimal actrecamt;//收款金额 实收金额
    private BigDecimal localamt;
    private String openorg;
    private boolean cico_ishavecontr;//是否合同收付 只能false
    private String sourcebillnumber;//源单编码 银行流水的 交易明细编号
    private String cico_billtypeid_number;//单据类型.编码 默认cas_recbill_BT
    private String org_number;
    private String accountbank_number;//银行账号
    //private String payee_company_number;//付款方，客户编码

    private String txt_description;//摘要1 收到xxx客户现金流
    private String currency_number;// 币别
    private String exratetable_number;//汇率表.编码
    private String cico_relateddepartments_number;//关联部门
    private String settletype_number;//结算方式

    @Data
    public class CQ2CollectionVOBody {
        private Long id;
        private BigDecimal e_discountamt;//收款明细.现金折扣
        private BigDecimal e_receivableamt;//收款明细.应收金额 实收金额
        private BigDecimal e_receivablelocamt;//应收则本币 = 应收金额
        private BigDecimal e_actamt;//收款明细.实收金额 实收金额
        private String e_settleorg_number;//结算组织.编码 默认“10000396”
        private String cico_customerfield_number;//客户.编码
        private String e_fundflowitem_number;//资金用途.编码 根据业务场景，与“资金用途”建立 mapping 关系，见 sheet：收款单-收款类型&资金用途
        private String cico_purposeoffunds_number;//国资委资金用途.编码 根据业务场景，与“国资委资金用途”建立 mapping 关系，见 sheet：收款单-收款类型&资金用途

    }
}
