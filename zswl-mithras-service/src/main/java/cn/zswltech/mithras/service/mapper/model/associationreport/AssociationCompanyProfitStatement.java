package cn.zswltech.mithras.service.mapper.model.associationreport;

import cn.zswltech.mithras.common.model.IEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @description 公司利润表数据表
 * @author vico
 * @date 2025-04-18
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AssociationCompanyProfitStatement extends BasicAssociationReport implements Serializable ,IEntity {

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
    * 主营业务收入_本季发生额(元)
    */
    @TableField("main_busi_incm_actm")
    private BigDecimal mainBusiIncmActm;

    /**
     * 主营业务收入_本年累计(元)
     */
    @TableField("main_busi_incm_tyag")
    private BigDecimal mainBusiIncmTyag;

    /**
     * 主营业务收入_去年同期(元)
     */
    @TableField("main_busi_incm_cply")
    private BigDecimal mainBusiIncmCply;



    /**
    * 主营业务成本_本季发生额(元)
    */
    @TableField("main_busi_cost_actm")
    private BigDecimal mainBusiCostActm;

    /**
     * 主营业务成本_本年累计(元)
     */
    @TableField("main_busi_cost_tyag")
    private BigDecimal mainBusiCostTyag;

    /**
     * 主营业务成本_去年同期(元)
     */
    @TableField("main_busi_cost_cply")
    private BigDecimal mainBusiCostCply;

    /**
    * 主营业务税金及附加_本季发生额(元)
    */
    @TableField("main_busi_tax_add_actm")
    private BigDecimal mainBusiTaxAddActm;

    /**
     * 主营业务税金及附加_本年累计(元)
     */
    @TableField("main_busi_tax_add_tyag")
    private BigDecimal mainBusiTaxAddTyag;

    /**
     * 主营业务税金及附加_去年同期(元)
     */
    @TableField("main_busi_tax_add_cply")
    private BigDecimal mainBusiTaxAddCply;

    /**
    * 主营业务利润_本季发生额(元)
    */
    @TableField("main_busi_prof_actm")
    private BigDecimal mainBusiProfActm;

    /**
     * 主营业务利润_本年累计(元)
     */
    @TableField("main_busi_prof_tyag")
    private BigDecimal mainBusiProfTyag;

    /**
     * 主营业务利润_去年同期(元)
     */
    @TableField("main_busi_prof_cply")
    private BigDecimal mainBusiProfCply;

    /**
    * 其他业务利润_本季发生额(元)
    */
    @TableField("oth_busi_prof_actm")
    private BigDecimal othBusiProfActm;

    /**
     * 其他业务利润_本年累计(元)
     */
    @TableField("oth_busi_prof_tyag")
    private BigDecimal othBusiProfTyag;

    /**
     * 其他业务利润_去年同期(元)
     */
    @TableField("oth_busi_prof_cply")
    private BigDecimal othBusiProfCply;

    /**
    * 营业费用_本季发生额(元)
    */
    @TableField("busi_fee_actm")
    private BigDecimal busiFeeActm;

    /**
     * 营业费用_本年累计(元)
     */
    @TableField("busi_fee_tyag")
    private BigDecimal busiFeeTyag;

    /**
     * 营业费用_去年同期(元)
     */
    @TableField("busi_fee_cply")
    private BigDecimal busiFeeCply;

    /**
    * 管理费用_本季发生额(元)
    */
    @TableField("mag_fee_actm")
    private BigDecimal magFeeActm;

    /**
     * 管理费用_本年累计(元)
     */
    @TableField("mag_fee_tyag")
    private BigDecimal magFeeTyag;

    /**
     * 管理费用_去年同期(元)
     */
    @TableField("mag_fee_cply")
    private BigDecimal magFeeCply;

    /**
    * 财务费用_本季发生额(元)
    */
    @TableField("fin_fee_actm")
    private BigDecimal finFeeActm;

    /**
     * 财务费用_本年累计(元)
     */
    @TableField("fin_fee_tyag")
    private BigDecimal finFeeTyag;

    /**
     * 财务费用_去年同期(元)
     */
    @TableField("fin_fee_cply")
    private BigDecimal finFeeCply;

    /**
    * 资产减值损失_本季发生额(元)
    */
    @TableField("ipoa_loss_actm")
    private BigDecimal ipoaLossActm;

    /**
     * 资产减值损失_本年累计(元)
     */
    @TableField("ipoa_loss_tyag")
    private BigDecimal ipoaLossTyag;

    /**
     * 资产减值损失_去年同期(元)
     */
    @TableField("ipoa_loss_cply")
    private BigDecimal ipoaLossCply;

    /**
    * 信用减值损失_本季发生额(元)
    */
    @TableField("cred_decr_loss_actm")
    private BigDecimal credDecrLossActm;

    /**
     * 信用减值损失_本年累计(元)
     */
    @TableField("cred_decr_loss_tyag")
    private BigDecimal credDecrLossTyag;

    /**
     * 信用减值损失_去年同期(元)
     */
    @TableField("cred_decr_loss_cply")
    private BigDecimal credDecrLossCply;

    /**
    * 营业利润_本季发生额(元)
    */
    @TableField("busi_prof_actm")
    private BigDecimal busiProfActm;

    /**
     * 营业利润_本年累计(元)
     */
    @TableField("busi_prof_tyag")
    private BigDecimal busiProfTyag;

    /**
     * 营业利润_去年同期(元)
     */
    @TableField("busi_prof_cply")
    private BigDecimal busiProfCply;

    /**
    * 投资收益_本季发生额(元)
    */
    @TableField("ivsm_payf_actm")
    private BigDecimal ivsmPayfActm;

    /**
     * 投资收益_本年累计(元)
     */
    @TableField("ivsm_payf_tyag")
    private BigDecimal ivsmPayfTyag;

    /**
     * 投资收益_去年同期(元)
     */
    @TableField("ivsm_payf_cply")
    private BigDecimal ivsmPayfCply;

    /**
    * 营业外收入_本季发生额(元)
    */
    @TableField("nopr_incm_actm")
    private BigDecimal noprIncmActm;

    /**
     * 营业外收入_本年累计(元)
     */
    @TableField("nopr_incm_tyag")
    private BigDecimal noprIncmTyag;

    /**
     * 营业外收入_去年同期(元)
     */
    @TableField("nopr_incm_cply")
    private BigDecimal noprIncmCply;

    /**
    * 营业外支出_本季发生额(元)
    */
    @TableField("nopr_pay_actm")
    private BigDecimal noprPayActm;

    /**
     * 营业外支出_本年累计(元)
     */
    @TableField("nopr_pay_tyag")
    private BigDecimal noprPayTyag;

    /**
     * 营业外支出_去年同期(元)
     */
    @TableField("nopr_pay_cply")
    private BigDecimal noprPayCply;

    /**
    * 利润总额_本季发生额(元)
    */
    @TableField("prof_gamt_actm")
    private BigDecimal profGamtActm;

    /**
     * 利润总额_本年累计(元)
     */
    @TableField("prof_gamt_tyag")
    private BigDecimal profGamtTyag;

    /**
     * 利润总额_去年同期(元)
     */
    @TableField("prof_gamt_cply")
    private BigDecimal profGamtCply;

    /**
    * 所得税费用_本季发生额(元)
    */
    @TableField("inct_fee_actm")
    private BigDecimal inctFeeActm;

    /**
     * 所得税费用_本年累计(元)
     */
    @TableField("inct_fee_tyag")
    private BigDecimal inctFeeTyag;

    /**
     * 所得税费用_去年同期(元)
     */
    @TableField("inct_fee_cply")
    private BigDecimal inctFeeCply;


    /**
    * 净利润_本季发生额(元)
    */
    @TableField("net_prof_actm")
    private BigDecimal netProfActm;

    /**
     * 净利润_本年累计(元)
     */
    @TableField("net_prof_tyag")
    private BigDecimal netProfTyag;

    /**
     * 净利润_去年同期(元)
     */
    @TableField("net_prof_cply")
    private BigDecimal netProfCply;


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
        // 主营业务利润 = 主营业务收入 - 主营业务成本 - 主营业务税金及附加
        mainBusiProfActm = mainBusiIncmActm.subtract(mainBusiCostActm).subtract(mainBusiTaxAddActm);
        mainBusiProfCply = mainBusiIncmCply.subtract(mainBusiCostCply).subtract(mainBusiTaxAddCply);
        mainBusiProfTyag = mainBusiIncmTyag.subtract(mainBusiCostTyag).subtract(mainBusiTaxAddTyag);
        // 营业利润 = 主营业务利润 + 其他业务利润 - 营业费用 - 管理费用 - 财务费用 - 信用减值损失（因为按照正数填列了，所以用减法）
        busiProfActm = mainBusiProfActm.add(othBusiProfActm).subtract(busiFeeActm).subtract(magFeeActm).subtract(finFeeActm).subtract(credDecrLossActm);
        busiProfCply = mainBusiProfCply.add(othBusiProfCply).subtract(busiFeeCply).subtract(magFeeCply).subtract(finFeeCply).subtract(credDecrLossCply);
        busiProfTyag = mainBusiProfTyag.add(othBusiProfTyag).subtract(busiFeeTyag).subtract(magFeeTyag).subtract(finFeeTyag).subtract(credDecrLossTyag);
        // 利润总额 = 营业利润 + 投资收益 + 营业外收入 - 营业外支出
        profGamtActm = busiProfActm.add(ivsmPayfActm).add(noprIncmActm).subtract(noprPayActm);
        profGamtCply = busiProfCply.add(ivsmPayfCply).add(noprIncmCply).subtract(noprPayCply);
        profGamtTyag = busiProfTyag.add(ivsmPayfTyag).add(noprIncmTyag).subtract(noprPayTyag);
        // 净利润 = 利润总额 - 所得税费用
        netProfActm = profGamtActm.subtract(inctFeeActm);
        netProfCply = profGamtCply.subtract(inctFeeCply);
        netProfTyag = profGamtTyag.subtract(inctFeeTyag);
    }

    @Override
    public void setMainId(Long id) {

    }

    @Override
    public Long getMainId() {
        return 0L;
    }
}
