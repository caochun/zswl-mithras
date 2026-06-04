package cn.zswltech.mithras.associationreport.mapper.model;
import cn.zswltech.mithras.service.mapper.tag.IEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @description 资产负债表
 * @author vico
 * @date 2025-04-18
 */
@Data
public class AssociationBalanceSheetPartial extends BasicAssociationReport implements Serializable , IEntity {

    private static final long serialVersionUID = 1L;

    /**
    * 自增主键
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
    * 行号 | 同一批次数据从1开始递增
    */
    @TableField("row_num")
    private Integer rowNum;

    /**
    * 企业统一社会信用代码
    */
    @TableField("unif_soci_cred_code")
    private String unifSociCredCode;

    /**
    * 货币资金_年初数(元)
    */
    @TableField("crcp_aboy")
    private BigDecimal crcpAboy;

    /**
    * 交易性金融资产_年初数(元)
    */
    @TableField("trd_finl_ast_aboy")
    private BigDecimal trdFinlAstAboy;

    /**
    * 衍生金融资产_年初数(元)
    */
    @TableField("devd_finl_ast_aboy")
    private BigDecimal devdFinlAstAboy;

    /**
    * 应收票据_年初数(元)
    */
    @TableField("recv_bill_aboy")
    private BigDecimal recvBillAboy;

    /**
    * 应收账款_年初数(元)
    */
    @TableField("recv_amt_aboy")
    private BigDecimal recvAmtAboy;

    /**
    * 应收款项融资_年初数(元)
    */
    @TableField("recv_amt_fin_aboy")
    private BigDecimal recvAmtFinAboy;

    /**
    * 预付账款_年初数(元)
    */
    @TableField("pia_amt_aboy")
    private BigDecimal piaAmtAboy;

    /**
    * 其他应收款总计_年初数(元)
    */
    @TableField("oth_recv_amt_tot_aboy")
    private BigDecimal othRecvAmtTotAboy;

    /**
    * 应收股利_年初数(元)
    */
    @TableField("recv_divd_aboy")
    private BigDecimal recvDivdAboy;

    /**
    * 应收利息_年初数(元)
    */
    @TableField("recv_intr_aboy")
    private BigDecimal recvIntrAboy;

    /**
    * 其他应收款_年初数(元)
    */
    @TableField("oth_recv_amt_aboy")
    private BigDecimal othRecvAmtAboy;

    /**
    * 存货_年初数(元)
    */
    @TableField("inv_aboy")
    private BigDecimal invAboy;

    /**
    * 合同资产_年初数(元)
    */
    @TableField("agmt_ast_aboy")
    private BigDecimal agmtAstAboy;

    /**
    * 持有待售资产_年初数(元)
    */
    @TableField("hold_sale_ast_aboy")
    private BigDecimal holdSaleAstAboy;

    /**
    * 一年内到期的非流动资产_年初数(元)
    */
    @TableField("ncoy_aboy")
    private BigDecimal ncoyAboy;

    /**
    * 其他流动资产_年初数(元)
    */
    @TableField("oth_liqd_ast_aboy")
    private BigDecimal othLiqdAstAboy;

    /**
    * 流动资产合计_年初数(元)
    */
    @TableField("liqd_ast_tot_aboy")
    private BigDecimal liqdAstTotAboy;
/*

    */
/**
     * 非流动资产_年初数(元)
     *//*

    @TableField("no_liqd_ast_tot_aboy")
    private BigDecimal NoLiqdAstTotAboy;
*/

    /**
    * 债权投资_年初数(元)
    */
    @TableField("clam_ivsm_aboy")
    private BigDecimal clamIvsmAboy;

    /**
    * 其他债权投资_年初数(元)
    */
    @TableField("oth_clam_ivsm_aboy")
    private BigDecimal othClamIvsmAboy;

    /**
    * 长期应收款_年初数(元)
    */
    @TableField("long_recv_amt_aboy")
    private BigDecimal longRecvAmtAboy;

    /**
    * 长期股权投资_年初数(元)
    */
    @TableField("lsri_aboy")
    private BigDecimal lsriAboy;

    /**
    * 其他权益工具投资_年初数(元)
    */
    @TableField("oth_equi_inst_ivsm_aboy")
    private BigDecimal othEquiInstIvsmAboy;

    /**
    * 其他非流动金融资产_年初数(元)
    */
    @TableField("oth_nocr_finl_ast_aboy")
    private BigDecimal othNocrFinlAstAboy;

    /**
    * 投资性房地产_年初数(元)
    */
    @TableField("ivsm_estt_aboy")
    private BigDecimal ivsmEsttAboy;

    /**
    * 固定资产_年初数(元)
    */
    @TableField("fix_ast_aboy")
    private BigDecimal fixAstAboy;

    /**
    * 在建工程_年初数(元)
    */
    @TableField("udcs_proj_aboy")
    private BigDecimal udcsProjAboy;

    /**
    * 生产性生物资产_年初数(元)
    */
    @TableField("prod_biol_matr_aboy")
    private BigDecimal prodBiolMatrAboy;

    /**
    * 油气资产_年初数(元)
    */
    @TableField("olgs_ast_aboy")
    private BigDecimal olgsAstAboy;

    /**
    * 使用权资产_年初数(元)
    */
    @TableField("use_ast_aboy")
    private BigDecimal useAstAboy;

    /**
    * 无形资产_年初数(元)
    */
    @TableField("imtr_ast_aboy")
    private BigDecimal imtrAstAboy;

    /**
    * 开发支出_年初数(元)
    */
    @TableField("dev_pay_aboy")
    private BigDecimal devPayAboy;

    /**
    * 商誉_年初数(元)
    */
    @TableField("gdwl_aboy")
    private BigDecimal gdwlAboy;

    /**
    * 长期待摊费用_年初数(元)
    */
    @TableField("long_tbat_fee_aboy")
    private BigDecimal longTbatFeeAboy;

    /**
    * 递延所得税资产_年初数(元)
    */
    @TableField("defr_tax_ast_aboy")
    private BigDecimal defrTaxAstAboy;

    /**
    * 其他非流动资产_年初数(元)
    */
    @TableField("oth_nocr_ast_aboy")
    private BigDecimal othNocrAstAboy;

    /**
    * 非流动资产合计_年初数(元)
    */
    @TableField("nocr_ast_tot_aboy")
    private BigDecimal nocrAstTotAboy;

    /**
    * 资产总计_年初数(元)
    */
    @TableField("ast_tot_aboy")
    private BigDecimal astTotAboy;

    /**
    * 短期借款_年初数(元)
    */
    @TableField("shtt_loan_aboy")
    private BigDecimal shttLoanAboy;

    /**
    * 交易性金融负债_年初数(元)
    */
    @TableField("trd_finl_liab_aboy")
    private BigDecimal trdFinlLiabAboy;

    /**
    * 衍生金融负债_年初数(元)
    */
    @TableField("devd_finl_liab_aboy")
    private BigDecimal devdFinlLiabAboy;

    /**
    * 应付票据_年初数(元)
    */
    @TableField("payb_bill_aboy")
    private BigDecimal paybBillAboy;

    /**
    * 应付账款_年初数(元)
    */
    @TableField("payb_amt_aboy")
    private BigDecimal paybAmtAboy;

    /**
    * 预收账款_年初数(元)
    */
    @TableField("ciad_amt_aboy")
    private BigDecimal ciadAmtAboy;

    /**
    * 合同负债_年初数(元)
    */
    @TableField("agmt_liab_aboy")
    private BigDecimal agmtLiabAboy;

    /**
    * 应付职工薪酬_年初数(元)
    */
    @TableField("payb_emp_cmps_aboy")
    private BigDecimal paybEmpCmpsAboy;

    /**
    * 应交税费_年初数(元)
    */
    @TableField("payc_tax_fee_aboy")
    private BigDecimal paycTaxFeeAboy;

    /**
    * 其他应付款总计_年初数(元)
    */
    @TableField("oth_payb_amt_tot_aboy")
    private BigDecimal othPaybAmtTotAboy;

    /**
    * 应付利息_年初数(元)
    */
    @TableField("payb_intr_aboy")
    private BigDecimal paybIntrAboy;

    /**
    * 应付股利_年初数(元)
    */
    @TableField("payb_divd_aboy")
    private BigDecimal paybDivdAboy;

    /**
    * 其他应付款_年初数(元)
    */
    @TableField("oth_payb_amt_aboy")
    private BigDecimal othPaybAmtAboy;

    /**
    * 持有待售负债_年初数(元)
    */
    @TableField("hold_sale_liab_aboy")
    private BigDecimal holdSaleLiabAboy;

    /**
    * 一年内到期的非流动负债_年初数(元)
    */
    @TableField("oney_nocr_liab_aboy")
    private BigDecimal oneyNocrLiabAboy;

    /**
    * 其他流动负债_年初数(元)
    */
    @TableField("oth_liqd_liab_aboy")
    private BigDecimal othLiqdLiabAboy;

    /**
    * 流动负债合计_年初数(元)
    */
    @TableField("liqd_liab_tot_aboy")
    private BigDecimal liqdLiabTotAboy;


    /**
    * 长期借款_年初数(元)
    */
    @TableField("long_loan_aboy")
    private BigDecimal longLoanAboy;

    /**
    * 应付债券_年初数(元)
    */
    @TableField("payb_bond_aboy")
    private BigDecimal paybBondAboy;

    /**
    * 长期应付款_年初数(元)
    */
    @TableField("long_payb_amt_aboy")
    private BigDecimal longPaybAmtAboy;

    /**
    * 长期应付职工薪酬_年初数(元)
    */
    @TableField("long_payb_emp_cmps_aboy")
    private BigDecimal longPaybEmpCmpsAboy;

    /**
    * 租赁负债_年初数(元)
    */
    @TableField("leas_liab_aboy")
    private BigDecimal leasLiabAboy;

    /**
    * 预计负债_年初数(元)
    */
    @TableField("expe_liab_aboy")
    private BigDecimal expeLiabAboy;

    /**
    * 递延收益_年初数(元)
    */
    @TableField("defr_payf_aboy")
    private BigDecimal defrPayfAboy;

    /**
    * 递延所得税负债_年初数(元)
    */
    @TableField("defr_inct_liab_aboy")
    private BigDecimal defrInctLiabAboy;

    /**
    * 其他非流动负债_年初数(元)
    */
    @TableField("oth_nocr_liab_aboy")
    private BigDecimal othNocrLiabAboy;

    /**
    * 非流动负债合计_年初数(元)
    */
    @TableField("nocr_liab_tot_aboy")
    private BigDecimal nocrLiabTotAboy;

    /**
    * 负债合计_年初数(元)
    */
    @TableField("liab_tot_aboy")
    private BigDecimal liabTotAboy;

    /**
    * 实收资本_年初数(元)
    */
    @TableField("paid_cptl_aboy")
    private BigDecimal paidCptlAboy;

    /**
    * 其他权益工具_年初数(元)
    */
    @TableField("oth_equi_inst_aboy")
    private BigDecimal othEquiInstAboy;

    /**
    * 资本公积_年初数(元)
    */
    @TableField("cptl_rsrv_aboy")
    private BigDecimal cptlRsrvAboy;

    /**
    * 其他综合收益_年初数(元)
    */
    @TableField("oth_cmph_payf_aboy")
    private BigDecimal othCmphPayfAboy;

    /**
    * 专项储备_年初数(元)
    */
    @TableField("spcl_rsrv_aboy")
    private BigDecimal spclRsrvAboy;

    /**
    * 盈余公积_年初数(元)
    */
    @TableField("surp_rsrv_aboy")
    private BigDecimal surpRsrvAboy;

    /**
    * 一般风险准备_年初数(元)
    */
    @TableField("risk_prep_aboy")
    private BigDecimal riskPrepAboy;

    /**
    * 未分配利润_年初数(元)
    */
    @TableField("no_assn_prof_aboy")
    private BigDecimal noAssnProfAboy;

    /**
    * 归属母公司所有者权益合计_年初数(元)
    */
    @TableField("attr_prn_cpon_equi_aboy")
    private BigDecimal attrPrnCponEquiAboy;

    /**
    * 少数股东权益_年初数(元)
    */
    @TableField("mish_equi_aboy")
    private BigDecimal mishEquiAboy;

    /**
    * 所有者权益合计_年初数(元)
    */
    @TableField("toeq_aboy")
    private BigDecimal toeqAboy;

    /**
    * 负债及所有者权益合计_年初数(元)
    */
    @TableField("liab_toeq_aboy")
    private BigDecimal liabToeqAboy;

    /**
    * 货币资金_期末数(元)
    */
    @TableField("crcp_aeop")
    private BigDecimal crcpAeop;

    /**
    * 交易性金融资产_期末数(元)
    */
    @TableField("trd_finl_ast_aeop")
    private BigDecimal trdFinlAstAeop;

    /**
    * 衍生金融资产_期末数(元)
    */
    @TableField("devd_finl_ast_aeop")
    private BigDecimal devdFinlAstAeop;

    /**
    * 应收票据_期末数(元)
    */
    @TableField("recv_bill_aeop")
    private BigDecimal recvBillAeop;

    /**
    * 应收账款_期末数(元)
    */
    @TableField("recv_amt_aeop")
    private BigDecimal recvAmtAeop;

    /**
    * 应收款项融资_期末数(元)
    */
    @TableField("recv_amt_fin_aeop")
    private BigDecimal recvAmtFinAeop;

    /**
    * 预付账款_期末数(元)
    */
    @TableField("pia_amt_aeop")
    private BigDecimal piaAmtAeop;

    /**
    * 其他应收款总计_期末数(元)
    */
    @TableField("oth_recv_amt_tot_aeop")
    private BigDecimal othRecvAmtTotAeop;

    /**
    * 应收股利_期末数(元)
    */
    @TableField("recv_divd_aeop")
    private BigDecimal recvDivdAeop;

    /**
    * 应收利息_期末数(元)
    */
    @TableField("recv_intr_aeop")
    private BigDecimal recvIntrAeop;

    /**
    * 其他应收款_期末数(元)
    */
    @TableField("oth_recv_amt_aeop")
    private BigDecimal othRecvAmtAeop;

    /**
    * 存货_期末数(元)
    */
    @TableField("inv_aeop")
    private BigDecimal invAeop;

    /**
    * 合同资产_期末数(元)
    */
    @TableField("agmt_ast_aeop")
    private BigDecimal agmtAstAeop;

    /**
    * 持有待售资产_期末数(元)
    */
    @TableField("hold_sale_ast_aeop")
    private BigDecimal holdSaleAstAeop;

    /**
    * 一年内到期的非流动资产_期末数(元)
    */
    @TableField("ncoy_aeop")
    private BigDecimal ncoyAeop;

    /**
    * 其他流动资产_期末数(元)
    */
    @TableField("oth_liqd_ast_aeop")
    private BigDecimal othLiqdAstAeop;

    /**
    * 流动资产合计_期末数(元)
    */
    @TableField("liqd_ast_tot_aeop")
    private BigDecimal liqdAstTotAeop;


    /**
    * 债权投资_期末数(元)
    */
    @TableField("clam_ivsm_aeop")
    private BigDecimal clamIvsmAeop;

    /**
    * 其他债权投资_期末数(元)
    */
    @TableField("oth_clam_ivsm_aeop")
    private BigDecimal othClamIvsmAeop;

    /**
    * 长期应收款_期末数(元)
    */
    @TableField("long_recv_amt_aeop")
    private BigDecimal longRecvAmtAeop;

    /**
    * 长期股权投资_期末数(元)
    */
    @TableField("lsri_aeop")
    private BigDecimal lsriAeop;

    /**
    * 其他权益工具投资_期末数(元)
    */
    @TableField("oth_equi_inst_ivsm_aeop")
    private BigDecimal othEquiInstIvsmAeop;

    /**
    * 其他非流动金融资产_期末数(元)
    */
    @TableField("oth_nocr_finl_ast_aeop")
    private BigDecimal othNocrFinlAstAeop;

    /**
    * 投资性房地产_期末数(元)
    */
    @TableField("ivsm_estt_aeop")
    private BigDecimal ivsmEsttAeop;

    /**
    * 固定资产_期末数(元)
    */
    @TableField("fix_ast_aeop")
    private BigDecimal fixAstAeop;

    /**
    * 在建工程_期末数(元)
    */
    @TableField("udcs_proj_aeop")
    private BigDecimal udcsProjAeop;

    /**
    * 生产性生物资产_期末数(元)
    */
    @TableField("prod_biol_matr_aeop")
    private BigDecimal prodBiolMatrAeop;

    /**
    * 油气资产_期末数(元)
    */
    @TableField("olgs_ast_aeop")
    private BigDecimal olgsAstAeop;

    /**
    * 使用权资产_期末数(元)
    */
    @TableField("use_ast_aeop")
    private BigDecimal useAstAeop;

    /**
    * 无形资产_期末数(元)
    */
    @TableField("imtr_ast_aeop")
    private BigDecimal imtrAstAeop;

    /**
    * 开发支出_期末数(元)
    */
    @TableField("dev_pay_aeop")
    private BigDecimal devPayAeop;

    /**
    * 商誉_期末数(元)
    */
    @TableField("gdwl_aeop")
    private BigDecimal gdwlAeop;

    /**
    * 长期待摊费用_期末数(元)
    */
    @TableField("long_tbat_fee_aeop")
    private BigDecimal longTbatFeeAeop;

    /**
    * 递延所得税资产_期末数(元)
    */
    @TableField("defr_tax_ast_aeop")
    private BigDecimal defrTaxAstAeop;

    /**
    * 其他非流动资产_期末数(元)
    */
    @TableField("oth_nocr_ast_aeop")
    private BigDecimal othNocrAstAeop;

    /**
    * 非流动资产合计_期末数(元)
    */
    @TableField("nocr_ast_tot_aeop")
    private BigDecimal nocrAstTotAeop;

    /**
    * 资产总计_期末数(元)
    */
    @TableField("ast_tot_aeop")
    private BigDecimal astTotAeop;

    /**
    * 短期借款_期末数(元)
    */
    @TableField("shtt_loan_aeop")
    private BigDecimal shttLoanAeop;

    /**
    * 交易性金融负债_期末数(元)
    */
    @TableField("trd_finl_liab_aeop")
    private BigDecimal trdFinlLiabAeop;

    /**
    * 衍生金融负债_期末数(元)
    */
    @TableField("devd_finl_liab_aeop")
    private BigDecimal devdFinlLiabAeop;

    /**
    * 应付票据_期末数(元)
    */
    @TableField("payb_bill_aeop")
    private BigDecimal paybBillAeop;

    /**
    * 应付账款_期末数(元)
    */
    @TableField("payb_amt_aeop")
    private BigDecimal paybAmtAeop;

    /**
    * 预收账款_期末数(元)
    */
    @TableField("ciad_amt_aeop")
    private BigDecimal ciadAmtAeop;

    /**
    * 合同负债_期末数(元)
    */
    @TableField("agmt_liab_aeop")
    private BigDecimal agmtLiabAeop;

    /**
    * 应付职工薪酬_期末数(元)
    */
    @TableField("payb_emp_cmps_aeop")
    private BigDecimal paybEmpCmpsAeop;

    /**
    * 应交税费_期末数(元)
    */
    @TableField("payc_tax_fee_aeop")
    private BigDecimal paycTaxFeeAeop;

    /**
    * 其他应付款总计_期末数(元)
    */
    @TableField("oth_payb_amt_tot_aeop")
    private BigDecimal othPaybAmtTotAeop;

    /**
    * 应付利息_期末数(元)
    */
    @TableField("payb_intr_aeop")
    private BigDecimal paybIntrAeop;

    /**
    * 应付股利_期末数(元)
    */
    @TableField("payb_divd_aeop")
    private BigDecimal paybDivdAeop;

    /**
    * 其他应付款_期末数(元)
    */
    @TableField("oth_payb_amt_aeop")
    private BigDecimal othPaybAmtAeop;

    /**
    * 持有待售负债_期末数(元)
    */
    @TableField("hold_sale_liab_aeop")
    private BigDecimal holdSaleLiabAeop;

    /**
    * 一年内到期的非流动负债_期末数(元)
    */
    @TableField("oney_nocr_liab_aeop")
    private BigDecimal oneyNocrLiabAeop;

    /**
    * 其他流动负债_期末数(元)
    */
    @TableField("oth_liqd_liab_aeop")
    private BigDecimal othLiqdLiabAeop;

    /**
    * 流动负债合计_期末数(元)
    */
    @TableField("liqd_liab_tot_aeop")
    private BigDecimal liqdLiabTotAeop;

    /**
    * 长期借款_期末数(元)
    */
    @TableField("long_loan_aeop")
    private BigDecimal longLoanAeop;

    /**
    * 应付债券_期末数(元)
    */
    @TableField("payb_bond_aeop")
    private BigDecimal paybBondAeop;

    /**
    * 长期应付款_期末数(元)
    */
    @TableField("long_payb_amt_aeop")
    private BigDecimal longPaybAmtAeop;

    /**
    * 长期应付职工薪酬_期末数(元)
    */
    @TableField("long_payb_emp_cmps_aeop")
    private BigDecimal longPaybEmpCmpsAeop;

    /**
    * 租赁负债_期末数(元)
    */
    @TableField("leas_liab_aeop")
    private BigDecimal leasLiabAeop;

    /**
    * 预计负债_期末数(元)
    */
    @TableField("expe_liab_aeop")
    private BigDecimal expeLiabAeop;

    /**
    * 递延收益_期末数(元)
    */
    @TableField("defr_payf_aeop")
    private BigDecimal defrPayfAeop;

    /**
    * 递延所得税负债_期末数(元)
    */
    @TableField("defr_inct_liab_aeop")
    private BigDecimal defrInctLiabAeop;

    /**
    * 其他非流动负债_期末数(元)
    */
    @TableField("oth_nocr_liab_aeop")
    private BigDecimal othNocrLiabAeop;

    /**
    * 非流动负债合计_期末数(元)
    */
    @TableField("nocr_liab_tot_aeop")
    private BigDecimal nocrLiabTotAeop;

    /**
    * 负债合计_期末数(元)
    */
    @TableField("liab_tot_aeop")
    private BigDecimal liabTotAeop;

    /**
    * 实收资本_期末数(元)
    */
    @TableField("paid_cptl_aeop")
    private BigDecimal paidCptlAeop;

    /**
    * 其他权益工具_期末数(元)
    */
    @TableField("oth_equi_inst_aeop")
    private BigDecimal othEquiInstAeop;

    /**
    * 资本公积_期末数(元)
    */
    @TableField("cptl_rsrv_aeop")
    private BigDecimal cptlRsrvAeop;

    /**
    * 其他综合收益_期末数(元)
    */
    @TableField("oth_cmph_payf_aeop")
    private BigDecimal othCmphPayfAeop;

    /**
    * 专项储备_期末数(元)
    */
    @TableField("spcl_rsrv_aeop")
    private BigDecimal spclRsrvAeop;

    /**
    * 盈余公积_期末数(元)
    */
    @TableField("surp_rsrv_aeop")
    private BigDecimal surpRsrvAeop;

    /**
    * 一般风险准备_期末数(元)
    */
    @TableField("risk_prep_aeop")
    private BigDecimal riskPrepAeop;

    /**
    * 未分配利润_期末数(元)
    */
    @TableField("no_assn_prof_aeop")
    private BigDecimal noAssnProfAeop;

    /**
    * 归属母公司所有者权益合计_期末数(元)
    */
    @TableField("attr_prn_cpon_equi_aeop")
    private BigDecimal attrPrnCponEquiAeop;

    /**
    * 少数股东权益_期末数(元)
    */
    @TableField("mish_equi_aeop")
    private BigDecimal mishEquiAeop;

    /**
    * 所有者权益合计_期末数(元)
    */
    @TableField("toeq_aeop")
    private BigDecimal toeqAeop;

    /**
    * 负债及所有者权益合计_期末数(元)
    */
    @TableField("liab_toeq_aeop")
    private BigDecimal liabToeqAeop;

    /**
    * 报表实例编号 uuid联合主键：(report_instance_id， row_num)
    */
    @TableField("report_instance_id")
    private String reportInstanceId;

    /**
    * 报表实例周期格式：yyyyqq
    */
    @TableField("report_instance_period")
    private String reportInstancePeriod;

    /**
    * 批次号
    */
    @TableField("batch_no")
    private String batchNo;

    /**
    * 版本号
    */
    @TableField("version")
    private String version;

    /**
    * 操作标识 新增数据为insert，更新数据记录时值为update
    */
    @TableField("op")
    private String op;

    /**
    * 上报时间
    */
    @TableField("report_time")
    private LocalDateTime reportTime;

    /**
    * 写入时间
    */
    @TableField("write_time")
    private LocalDateTime writeTime;

    public void calculate() {
//        // 其他应付款总计 = 应付利息 + 应付股利 + 其他应付款
//        othPaybAmtTotAboy = paybIntrAboy.add(paybDivdAboy).add(othPaybAmtAboy);
//        othPaybAmtTotAeop = paybIntrAeop.add(paybDivdAeop).add(othPaybAmtAeop);
//        // 其他应收款总计 = 应收利息 + 应收股利 + 其他应收款
//        othRecvAmtTotAboy = recvIntrAboy.add(recvDivdAboy).add(othRecvAmtAboy);
//        othRecvAmtTotAeop = recvIntrAeop.add(recvDivdAeop).add(othRecvAmtAeop);
    }

    @Override
    public void setMainId(Long id) {

    }

    @Override
    public Long getMainId() {
        return 0L;
    }
}
