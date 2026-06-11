package cn.zswltech.mithras.third.overduereport.client.req;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 应收逾期集成保存接口
 * @author: jackerhe
 **/
@Data
public class OverdueReportSetBatSaveReq {

    // 必填字段
    public String cico_ovedue_no;                // 收款计划编号-集成单苍穹编号
    public String cico_ovedue_origin_no;         // 收款计划源单编号
    public String cico_origin_no;                 // 结算记录源单编号
    public String cico_set_date;                    // 结算日期
    public String cico_doc_date;                    // 结算记录的凭证记账日期
    public BigDecimal cico_set_amount;            // 结算金额(原币)
    public BigDecimal cico_set_amount_f;          // 结算金额(本位币)
    public String cico_set_relation;              // 结算关系（见表格）
    public String cico_account_org_number;         // 核算组织.编码

    // 非必填字段
    public String cico_origin_sys;                 // 来源系统[1:待定]
    public String cico_origin_type;                // 源单类型
    public String cico_ovedue_no_integration;      // 收款计划编号-集成单苍穹编号（原注释重复字段）

}