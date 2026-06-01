package cn.zswltech.mithras.service.service.third.financial.vo;

import cn.zswltech.mithras.service.service.third.financial.req.CQ2CommonReq;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;


/**
 * 苍穹2期 通用应收单 只保留了必填的基本数据
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class CQ2PlanCollectionVO extends CQ2CommonReq {
    private String requestId;//非必传参数，防止接口被重复调用 y
    private String cico_srcbillno;//单据编号 不填，财务系统自动生成
    private String bizdate;//单据日期-最后一笔实收日期 y
    private String cico_acctagebegining;//同单据日期
    private String asstacttype;//往来类型 [bd_customer:客户,bd_supplier:供应商,bos_user:人员].1. 厂商质保金，默认“bd_supplier”其他都默认“bd_customer”y
    private BigDecimal exchangerate;//汇率 默认“1” y
    private Boolean cico_isinvoice;//是否开票
    private String cico_srcsystem;// RZY
    private String creator_number;//创建人.工号 y
    private String org_number;//结算组织.编码 默认“10000396” y
    private String department_number;//部门.编码 业务部门 y
    private String asstact_number;//客户.编码 客户编码 y
    private String recorg_number;//收款组织.编码 默认“10000396” y
    private String cico_applyorg_number;//申请开票组织.编码 默认“10000396” y
    private String currency_number;//结算币别.货币代码 默认”RMB" y 新的CNY
    private String cico_paynum_rby;//交易流水号 银行流水ID y.需等流水中心改造后获取
    private String cico_contractnum_number;//合同编号 ---新接口这里不允许填
    private String cico_comment;//取合同的核算规则：剩余本金法 or 实际利率法 y
    private String billtype_number;//单据类型 Y

    private List<CQ2PlanCollectionVOBody> entry;

    @Data
    public class CQ2PlanCollectionVOBody{
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
        private String e_remark;//明细.备注取合同的核算规则：剩余本金法 or 实际利率法 y
        private String cico_incomeitems_number;//收入项目.编码 根据现金流项目，与“收入项目”建立 mapping 关系，见sheet：应收单-收入项目 y
        private String cico_e_businesstype_number;//业务类型.编码 y
        private String project_name;//项目.项目编码 合同编号 y
    }
}
