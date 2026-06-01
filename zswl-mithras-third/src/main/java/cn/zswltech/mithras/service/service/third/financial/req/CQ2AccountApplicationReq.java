package cn.zswltech.mithras.service.service.third.financial.req;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;
/**
 * 苍穹2期 记账申请单
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class CQ2AccountApplicationReq extends CQ2CommonReq {
    private String description; // 说明 枚举值如下：项目起租、项目变更（note：包括租金表调整或提前结清等涉及到租金表的调整的都算）、计提利息（剩余本金法）、计提利息（实际利率法）、拨备、计提融资成本、计提印花税 Y
    private String dept_number; // 部门.编码 业务部门 Y
    private String creator_number; // 创建人.工号 默认 获取 token 的账号 Y
    private String tallycompany_number; // 核算组织.编码 默认“10000396” Y
    private String cico_system; // 来源系统 [JWWZ:金温物资系统, RZYXT:融租易系统, JGYWXT:交工业务系统] 默认“RZYXT” Y
    private String company_number; // 申请人公司.编码 默认“10000396” Y
    private String bizdate; // 申请日期 系统日 Y
    private String tallydate; // 记账日期 系统日 Y
    private String cico_period_number; // 记账期间.编码 示例：<2024>年<4>期：按字段1 description 的类型区分：1）项目起租：取起租日的年月 2）项目变更：取变更生效的年月 3）计提利息或拨备或融资成本：取【月结管理】中所筛选的年月 4）计提印花税：取【月结管理】中所筛选的年月 Y
    private String cico_customer; // 客户.编码 客户编码 Y
    private String cico_taxcategory;//一般计税:	normal 简易计税:	simple
    private String cico_sourcebillno;//来源系统单据编号
    private String mainbiztype_number;//报账业务类型.编码
    private String cico_ishavecontr;//是否合同收付 y
    private String cico_billtype_number;//填默认。。。必填但我方没有
    private List<Entry> tallyentryentity; // 收款明细 [{}] 下面是明细 Y

    @Data
    public class Entry {

        private String businessdate; // 记账明细.业务日期 系统日 Y
        private BigDecimal cico_amount; // 记账明细.金额 按字段1 description 的类型区分：1）项目起租：租金总额 2）项目变更：租金表的每次变更，给租金变更的差额，允许负数。3）计提利息收入：【收入】列总和 3）计提融资成本：【成本】列总和 3）计提拨备：4）计提印花税： Y
        private BigDecimal cico_bhsje; // 记账明细.不含税金额 按字段1 description 的类型区分：1）项目起租：起租流程中的 不含税金额 2）项目变更：3）计提利息收入：【不含税收入】列总和 3）计提融资成本：3）计提拨备：4）计提印花税： Y
        private String cico_contract_num; // 记账明细.合同编号 合同编号 Y
        private String cico_custname; // 记账明细.客户名称 客户名称 Y
        private BigDecimal cico_hsje; // 记账明细.含税金额 同 字段20“记账明细.金额” Y
        private BigDecimal cico_se; // 记账明细.税额 按字段1 description 的类型区分：1）项目起租：起租流程中的 不含税金额 2）项目变更：3）计提利息收入：【税额】列总和 3）计提融资成本：3）计提拨备：4）计提印花税： Y 待补充计算规则
        private BigDecimal tallyamount; // 记账明细.记账金额 同 字段20“记账明细.金额” Y
        //private String cico_entryproject; // 项目.项目编码 合同编号 Y
        private String cico_ywlxtyoe_number; // 业务类型.编码 枚举值：售后回租、直租、经营性租赁、保理、转租赁、债权转让 Y
        private String tallyexplanation;
        /**
         * "根据记账明细.说明 判断
         *          非计提成本的场景：客商编号
         *          计提成本：
         *                         1）间融-银行
         *                         2）间融-租赁、集团：客商编号
         *                         3）直融：默认其他“fbzdy999999”"
         **/
        private String cico_financialins;
        private String e_asstact;//"项目端：客户的客户编码 融资端：默认“10000396”"
        private String cico_financialins_number;//记账申请单
        private String tallydeptid_number;//部门
        private String customer_number;//客户编码
        private String cico_project2_name;//合同号;//合同号
        private String cico_sl_number;//税率

    }
}