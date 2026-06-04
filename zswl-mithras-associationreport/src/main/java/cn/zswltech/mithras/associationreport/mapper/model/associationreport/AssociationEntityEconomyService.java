package cn.zswltech.mithras.associationreport.mapper.model;

import cn.zswltech.mithras.service.mapper.tag.IEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @description 实体经济服务数据表
 * @author vico
 * @date 2025-04-18
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AssociationEntityEconomyService extends BasicAssociationReport implements Serializable, IEntity {

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

    /**
    * 报表实例id | uuid
    */
    @TableField("report_instance_id")
    private String reportInstanceId;

    /**
    * 报表周期 | yyyyqq格式
    */
    @TableField("report_instance_period")
    private String reportInstancePeriod;

    /**
    * 批次号 | 0000-9999
    */
    @TableField("batch_no")
    private String batchNo;

    /**
    * 版本号 | 周期版本
    */
    @TableField("version")
    private String version;

    /**
    * 操作标识 | insert/update
    */
    @TableField("op")
    private String op;

    /**
    * 上报时间
    */
    @TableField("report_time")
    private LocalDateTime reportTime;

    /**
    * 入库时间
    */
    @TableField("write_time")
    private LocalDateTime writeTime;

    public void calculate() {
        // 本年租赁业务累计投放额-本期发生额 = 期末数 - 期初数
        this.tyagLeasBusiAmtAotc = this.tyagLeasBusiAmtAeop.subtract(this.tyagLeasBusiAmtAbop);
        // 其中：本年制造业租赁累计投放额-本期发生额 = 期末数 - 期初数
        this.tyagMnftLeasAmtAotc = this.tyagMnftLeasAmtAeop.subtract(this.tyagMnftLeasAmtAbop);
        // 本年服务产业链租赁累计投放额-本期发生额 = 不填充
        // 本年服务民生消费租赁累计投放额-本期发生额 = 期末数 - 期初数
        this.tyagConsLeasAmtAotc = this.tyagConsLeasAmtAeop.subtract(this.tyagConsLeasAmtAbop);
        // 本年科技金融建设租赁累计投放额-本期发生额 = 期末数 - 期初数
        this.tyagSatyLeasAmtAotc = this.tyagSatyLeasAmtAeop.subtract(this.tyagSatyLeasAmtAbop);
        // 本年绿色金融建设租赁累计投放额-本期发生额 = 期末数 - 期初数
        this.tyagGrenLeasAmtAotc = this.tyagGrenLeasAmtAeop.subtract(this.tyagGrenLeasAmtAbop);
        // 本年普惠金融建设租赁累计投放额-本期发生额 = 期末数 - 期初数
        this.tyagIcveLeasAmtAotc = this.tyagIcveLeasAmtAeop.subtract(this.tyagIcveLeasAmtAbop);
        // 本年养老金融建设租赁累计投放额-本期发生额 = 不填充
        // 本年海洋金融建设租赁累计投放额-本期发生额 = 期末数 - 期初数
        this.tyagOceaLeasAmtAotc = this.tyagOceaLeasAmtAeop.subtract(this.tyagOceaLeasAmtAbop);
        // 本年开放金融建设租赁累计投放额-本期发生额 = 不填充
        // 本年累计服务客户数-本期发生额 = 期末数 - 期初数
        this.tyagServCustNumAotc = this.tyagServCustNumAeop - this.tyagServCustNumAbop;
        // 历年累计租赁业务金额（含本年）-期末数 = 期初数 + 本年租赁业务累计投放额-本期发生额
        this.oyagLeasBusiAmtAeop = this.oyagLeasBusiAmtAbop.add(this.tyagLeasBusiAmtAotc);
        // 历年累计租赁业务金额（含本年）-本期发生额 = 期末数 - 期初数
        this.oyagLeasBusiAmtAotc = this.oyagLeasBusiAmtAeop.subtract(this.oyagLeasBusiAmtAbop);
        // 历年累计服务客户数（含本年）-期末数 = 期初数 + 本年累计服务客户数-本期发生额
        this.oyagServCustNumAeop = this.oyagServCustNumAbop + this.tyagServCustNumAotc;
        // 历年累计服务客户数（含本年）-本期发生额 = 期末数 - 期初数
        this.oyagServCustNumAotc = this.oyagServCustNumAeop - this.oyagServCustNumAbop;
        // 本年累计实际缴纳各类税金-本期发生额 = 期末数 - 期初数
        this.thsyTaxpAmtAotc = this.thsyTaxpAmtAeop.subtract(this.thsyTaxpAmtAbop);
        // 其中：（1）增值税-本期发生额 = 期末数 - 期初数
        this.incrTaxAotc = this.incrTaxAeop.subtract(this.incrTaxAbop);
        // （2）企业所得税-本期发生额 = 期末数 - 期初数
        this.corpInctAotc = this.corpInctAeop.subtract(this.corpInctAbop);
        // （3）其他税金-期末数 = 本年累计实际缴纳各类税金-期末数 - 增值税-期末数 - 企业所得税-期末数
        this.othTaxAeop = this.thsyTaxpAmtAeop.subtract(this.incrTaxAeop).subtract(corpInctAeop);
        // （3）其他税金-本期发生额 = 期末数 - 期初数
        this.othTaxAotc = this.othTaxAeop.subtract(this.othTaxAbop);
        // 历年累计实际缴纳各类税金（含本年）-期末数 = 期初数 + 本年累计实际缴纳各类税金-本期发生额
        this.otyTaxpAmtAeop = this.otyTaxpAmtAbop.add(this.thsyTaxpAmtAotc);
        // 历年累计实际缴纳各类税金（含本年）-本期发生额 = 期末数 - 期初数
        this.otyTaxpAmtAotc = this.otyTaxpAmtAeop.subtract(this.otyTaxpAmtAbop);
    }

    @Override
    public void setMainId(Long id) {

    }

    @Override
    public Long getMainId() {
        return 0L;
    }
}
