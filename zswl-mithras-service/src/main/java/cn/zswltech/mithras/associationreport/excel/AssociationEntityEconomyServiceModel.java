package cn.zswltech.mithras.associationreport.excel;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author 实体经济服务数据表
 * @date 2025/4/22
 * @description
 */
@Data
public class AssociationEntityEconomyServiceModel  extends AssociationReportBaseModel  {


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
     * 本年累计租赁业务金额_期初数(万元)
     */
    @TableField("tyag_leas_busi_amt_abop")
    private BigDecimal tyagLeasBusiAmtAbop;

    /**
     * 本年累计租赁业务金额_本期发生额(万元)
     */
    @TableField("tyag_leas_busi_amt_aotc")
    private BigDecimal tyagLeasBusiAmtAotc;

    /**
     * 本年累计租赁业务金额_期末数(万元)
     */
    @TableField("tyag_leas_busi_amt_aeop")
    private BigDecimal tyagLeasBusiAmtAeop;

    /**
     * 制造业租赁_期初数(万元)
     */
    @TableField("tyag_mnft_leas_amt_abop")
    private BigDecimal tyagMnftLeasAmtAbop;

    /**
     * 制造业租赁_发生额(万元)
     */
    @TableField("tyag_mnft_leas_amt_aotc")
    private BigDecimal tyagMnftLeasAmtAotc;

    /**
     * 制造业租赁_期末数(万元)
     */
    @TableField("tyag_mnft_leas_amt_aeop")
    private BigDecimal tyagMnftLeasAmtAeop;

    /**
     * 产业链租赁_期初数(万元)
     */
    @TableField("tyag_indt_leas_amt_abop")
    private BigDecimal tyagIndtLeasAmtAbop;

    /**
     * 产业链租赁_发生额(万元)
     */
    @TableField("tyag_indt_leas_amt_aotc")
    private BigDecimal tyagIndtLeasAmtAotc;

    /**
     * 产业链租赁_期末数(万元)
     */
    @TableField("tyag_indt_leas_amt_aeop")
    private BigDecimal tyagIndtLeasAmtAeop;

    /**
     * 民生消费租赁_期初数(万元)
     */
    @TableField("tyag_cons_leas_amt_abop")
    private BigDecimal tyagConsLeasAmtAbop;

    /**
     * 民生消费租赁_发生额(万元)
     */
    @TableField("tyag_cons_leas_amt_aotc")
    private BigDecimal tyagConsLeasAmtAotc;

    /**
     * 民生消费租赁_期末数(万元)
     */
    @TableField("tyag_cons_leas_amt_aeop")
    private BigDecimal tyagConsLeasAmtAeop;

    /**
     * 科技金融租赁_期初数(万元)
     */
    @TableField("tyag_saty_leas_amt_abop")
    private BigDecimal tyagSatyLeasAmtAbop;

    /**
     * 科技金融租赁_发生额(万元)
     */
    @TableField("tyag_saty_leas_amt_aotc")
    private BigDecimal tyagSatyLeasAmtAotc;

    /**
     * 科技金融租赁_期末数(万元)
     */
    @TableField("tyag_saty_leas_amt_aeop")
    private BigDecimal tyagSatyLeasAmtAeop;

    /**
     * 绿色金融租赁_期初数(万元)
     */
    @TableField("tyag_gren_leas_amt_abop")
    private BigDecimal tyagGrenLeasAmtAbop;

    /**
     * 绿色金融租赁_发生额(万元)
     */
    @TableField("tyag_gren_leas_amt_aotc")
    private BigDecimal tyagGrenLeasAmtAotc;

    /**
     * 绿色金融租赁_期末数(万元)
     */
    @TableField("tyag_gren_leas_amt_aeop")
    private BigDecimal tyagGrenLeasAmtAeop;

    /**
     * 普惠金融租赁_期初数(万元)
     */
    @TableField("tyag_icve_leas_amt_abop")
    private BigDecimal tyagIcveLeasAmtAbop;

    /**
     * 普惠金融租赁_发生额(万元)
     */
    @TableField("tyag_icve_leas_amt_aotc")
    private BigDecimal tyagIcveLeasAmtAotc;

    /**
     * 普惠金融租赁_期末数(万元)
     */
    @TableField("tyag_icve_leas_amt_aeop")
    private BigDecimal tyagIcveLeasAmtAeop;

    /**
     * 养老金融租赁_期初数(万元)
     */
    @TableField("tyag_pens_leas_amt_abop")
    private BigDecimal tyagPensLeasAmtAbop;

    /**
     * 养老金融租赁_发生额(万元)
     */
    @TableField("tyag_pens_leas_amt_aotc")
    private BigDecimal tyagPensLeasAmtAotc;

    /**
     * 养老金融租赁_期末数(万元)
     */
    @TableField("tyag_pens_leas_amt_aeop")
    private BigDecimal tyagPensLeasAmtAeop;

    /**
     * 海洋金融租赁_期初数(万元)
     */
    @TableField("tyag_ocea_leas_amt_abop")
    private BigDecimal tyagOceaLeasAmtAbop;

    /**
     * 海洋金融租赁_发生额(万元)
     */
    @TableField("tyag_ocea_leas_amt_aotc")
    private BigDecimal tyagOceaLeasAmtAotc;

    /**
     * 海洋金融租赁_期末数(万元)
     */
    @TableField("tyag_ocea_leas_amt_aeop")
    private BigDecimal tyagOceaLeasAmtAeop;

    /**
     * 开放金融租赁_期初数(万元)
     */
    @TableField("tyag_open_leas_amt_abop")
    private BigDecimal tyagOpenLeasAmtAbop;

    /**
     * 开放金融租赁_发生额(万元)
     */
    @TableField("tyag_open_leas_amt_aotc")
    private BigDecimal tyagOpenLeasAmtAotc;

    /**
     * 开放金融租赁_期末数(万元)
     */
    @TableField("tyag_open_leas_amt_aeop")
    private BigDecimal tyagOpenLeasAmtAeop;

    /**
     * 服务客户数_期初数
     */
    @TableField("tyag_serv_cust_num_abop")
    private Integer tyagServCustNumAbop;

    /**
     * 服务客户数_发生额
     */
    @TableField("tyag_serv_cust_num_aotc")
    private Integer tyagServCustNumAotc;

    /**
     * 服务客户数_期末数
     */
    @TableField("tyag_serv_cust_num_aeop")
    private Integer tyagServCustNumAeop;

    /**
     * 历年租赁金额_期初(万元)
     */
    @TableField("oyag_leas_busi_amt_abop")
    private BigDecimal oyagLeasBusiAmtAbop;

    /**
     * 历年租赁金额_发生(万元)
     */
    @TableField("oyag_leas_busi_amt_aotc")
    private BigDecimal oyagLeasBusiAmtAotc;

    /**
     * 历年租赁金额_期末(万元)
     */
    @TableField("oyag_leas_busi_amt_aeop")
    private BigDecimal oyagLeasBusiAmtAeop;

    /**
     * 历年客户数_期初
     */
    @TableField("oyag_serv_cust_num_abop")
    private Integer oyagServCustNumAbop;

    /**
     * 历年客户数_发生
     */
    @TableField("oyag_serv_cust_num_aotc")
    private Integer oyagServCustNumAotc;

    /**
     * 历年客户数_期末
     */
    @TableField("oyag_serv_cust_num_aeop")
    private Integer oyagServCustNumAeop;

    /**
     * 实缴税金_期初(万元)
     */
    @TableField("thsy_taxp_amt_abop")
    private BigDecimal thsyTaxpAmtAbop;

    /**
     * 实缴税金_发生(万元)
     */
    @TableField("thsy_taxp_amt_aotc")
    private BigDecimal thsyTaxpAmtAotc;

    /**
     * 实缴税金_期末(万元)
     */
    @TableField("thsy_taxp_amt_aeop")
    private BigDecimal thsyTaxpAmtAeop;

    /**
     * 增值税_期初(万元)
     */
    @TableField("incr_tax_abop")
    private BigDecimal incrTaxAbop;

    /**
     * 增值税_发生(万元)
     */
    @TableField("incr_tax_aotc")
    private BigDecimal incrTaxAotc;

    /**
     * 增值税_期末(万元)
     */
    @TableField("incr_tax_aeop")
    private BigDecimal incrTaxAeop;

    /**
     * 企业所得税_期初(万元)
     */
    @TableField("corp_inct_abop")
    private BigDecimal corpInctAbop;

    /**
     * 企业所得税_发生(万元)
     */
    @TableField("corp_inct_aotc")
    private BigDecimal corpInctAotc;

    /**
     * 企业所得税_期末(万元)
     */
    @TableField("corp_inct_aeop")
    private BigDecimal corpInctAeop;

    /**
     * 其他税金_期初(万元)
     */
    @TableField("oth_tax_abop")
    private BigDecimal othTaxAbop;

    /**
     * 其他税金_发生(万元)
     */
    @TableField("oth_tax_aotc")
    private BigDecimal othTaxAotc;

    /**
     * 其他税金_期末(万元)
     */
    @TableField("oth_tax_aeop")
    private BigDecimal othTaxAeop;

    /**
     * 历年税金_期初(万元)
     */
    @TableField("oty_taxp_amt_abop")
    private BigDecimal otyTaxpAmtAbop;

    /**
     * 历年税金_发生(万元)
     */
    @TableField("oty_taxp_amt_aotc")
    private BigDecimal otyTaxpAmtAotc;

    /**
     * 历年税金_期末(万元)
     */
    @TableField("oty_taxp_amt_aeop")
    private BigDecimal otyTaxpAmtAeop;

    /**
     * 表外业务_期初(万元)
     */
    @TableField("ofbl_amt_abop")
    private BigDecimal ofblAmtAbop;

    /**
     * 表外业务_发生(万元)
     */
    @TableField("ofbl_amt_aotc")
    private BigDecimal ofblAmtAotc;

    /**
     * 表外业务_期末(万元)
     */
    @TableField("ofbl_amt_aeop")
    private BigDecimal ofblAmtAeop;

    
}
