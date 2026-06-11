package cn.zswltech.mithras.kpi.mapper.model;
import cn.zswltech.mithras.kpi.enums.KpiProjectWeightTypeEnum;
import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * @description 绩效-项目测算分配表
 * @author vico
 * @date 2023-06-16
 */
@Data
public class KpiProjGuessDivide extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * id
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("contract_id")
    private Long contractId;

    /**
    * 项目测算表id
    */
    @TableField("kpi_proj_guess_id")
    private Long kpiProjGuessId;

    /**
     * 分配类型 人/部门
     * 分配比重类型 {@link KpiProjectWeightTypeEnum#name()}
     **/
    @TableField("divide_type")
    private String divideType;

    /**
    * 分配目标id
    */
    @TableField("divide_target")
    private Long divideTarget;

    @TableField("divide_weight")
    private Integer divideWeight;

    /**
    * 利润提奖-当期值
    */
    @TableField("profit_current")
    private Long profitCurrent;

    /**
    * 利润提奖-累计值
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
    * 奖金-累计值
    */
    @TableField("bonus_total")
    private Long bonusTotal;

    /**
     * 奖金-调整值
     */
    @TableField("bonus_adjust")
    private Long bonusAdjust;

    /**
     * 投放奖金-当期值
     */
    @TableField("payment_current")
    private Long paymentCurrent;

    /**
     * 投放奖金-累计值
     */
    @TableField("payment_total")
    private Long paymentTotal;

    /**
     * 投放提奖-当期值
     */
    @TableField("payment_award_current")
    private Long paymentAwardCurrent;

    /**
     * 投放提奖-累计值
     */
    @TableField("payment_award_total")
    private Long paymentAwardTotal;


    /**
    * 分配年
    */
    @TableField("divide_year")
    private Integer divideYear;

    /**
    * 分配月
    */
    @TableField("divide_month")
    private Integer divideMonth;

    @TableField("dept_id")
    private Long deptId;

    //基础提奖比例
    @TableField("project_radio_config")
    private String projectRadioConfig;
    //项目规模系数
    @TableField("scale_radio_config")
    private String scaleRadioConfig;
    //项目类型系数
    @TableField("type_radio_config")
    private String typeRadioConfig;
    //投放奖金系数
    @TableField("payment_bonus_radio_config")
    private String paymentBonusRadioConfig;
    //项目利润当期值
    @TableField("project_profit")
    private Long projectProfit;

}
