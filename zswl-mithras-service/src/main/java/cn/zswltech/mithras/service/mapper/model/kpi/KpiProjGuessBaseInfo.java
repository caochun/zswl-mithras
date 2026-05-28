package cn.zswltech.mithras.service.mapper.model.kpi;

import cn.zswltech.mithras.service.enums.kpi.KpiProjectClassifyEnum;
import cn.zswltech.mithras.service.enums.kpi.KpiProjectSourceDistributionEnum;
import cn.zswltech.mithras.service.mapper.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @description 绩效-项目测算表
 * @author jackerhe
 * @date 2023-06-15
 */
@Data
public class KpiProjGuessBaseInfo extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * id
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
    * 项目分配表id
    */
    @TableField("kpi_project_distribution_id")
    private Long kpiProjectDistributionId;

    /**
    * 项目分配表版本
    */
    @TableField("kpi_project_distribution_version")
    private String kpiProjectDistributionVersion;

    /**
    * 合同id
    */
    @TableField("contract_id")
    private Long contractId;


    /**
     * 借据id
     **/
    @TableField("receipt_id")
    private Long receiptId;

    /**
     * 借据id
     **/
    @TableField("receipt_first_payment_date")
    private LocalDate receiptFirstPaymentDate;

    /**
     * 项目类别 {@link KpiProjectClassifyEnum#name()}
     */
    @TableField("proj_classify")
    private String projClassify;

    /**
     * 项目来源 {@link KpiProjectSourceDistributionEnum#name()}
     * 新增 首次投放在本年的
     */
    @TableField("proj_source")
    private String projSource;

    /**
    * 利润-当期值
    */
    @TableField("profit_current")
    private Long profitCurrent;

    /**
    * 利润-累计值
    */
    @TableField("profit_total")
    private Long profitTotal;

    /**
     * 利润提奖-调整值
     */
    @TableField("profit_adjust")
    private Long profitAdjust;

    /**
    * 奖金-当期值
    */
    @TableField("bonus_current")
    private Long bonusCurrent;

    /**
    * 奖金-l累计值
    */
    @TableField("bonus_total")
    private Long bonusTotal;

    /**
    * 核算日期年
    */
    @TableField("calculate_date_year")
    private Integer calculateDateYear;

    /**
    * 核算日期月
    */
    @TableField("calculate_date_month")
    private Integer calculateDateMonth;


    /**
     * 投放-当期值
     */
    @TableField("payment_current")
    private Long paymentCurrent;

    /**
     * 投放-累计值
     */
    @TableField("payment_total")
    private Long paymentTotal;

    //项目本年投放金额
    @TableField("proj_payment_year_amount")
    private Long projPaymentYearAmount;

    //合同本月投放金额
    @TableField("contract_payment_month_amount")
    private Long contractPaymentMonthAmount;

    /**
    * 提奖比例
    */
    @TableField("award_ratio")
    private Long awardRatio;

    /**
     * 提奖比例配置快照
     */
    @TableField("award_ratio_config")
    private String awardRatioConfig;

}
