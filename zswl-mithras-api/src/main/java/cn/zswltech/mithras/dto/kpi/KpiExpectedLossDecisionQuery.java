package cn.zswltech.mithras.dto.kpi;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 预期信用损失减值ECL服务 引擎请求参数
 */
@Data
public class KpiExpectedLossDecisionQuery {

    /**
    * 合同号
    */
    private String order_num;

    /**
     * 承租人
     */
    private String actualClientName;
    /**
     * 承租人
     */
    private Long actualClientId;

    /**
    * 评估主体
    */
    private String client_name;

    private Long clientId;

    /**
     * 评估主体内评
     */
    private String inner_level;

    /**
     * 评估主体内评违约概率PD
     */
    private BigDecimal inner_pd;

    /**
     * md评级转pd
     **/
    private String ecl_breach_mapping_map;

    /**
     * 所属分组
     */
    private String group;

    /**
     * 五级分类
     */
    private String classify;

    /**
     * 逾期天数
     */
    private Long late_day;

    /**
     * 最近逾期时间
     **/
    private LocalDate lateDate;

    /**
     * 租赁物类型
     */
    private String lease_type;

    /**
     * 剩余本金
     */
    private BigDecimal remain_principal;

    /**
     * 应计利息
     */
    private BigDecimal accrued_interest;

    /**
     * 保证金
     */
    private BigDecimal deposit;

    /**
     * 合同到期日
     */
    private LocalDate expire_day;

    /**
     * 评估主体初始内评
     */
    private String inner_first_level;

    /**
     * 外部评级
     **/
    private String outer_level;

    private String ecl_out_pd;

    /**
     * ECL基准调整因子Z
     */
    private String rzy_ecl_base_z;

    /**
     * ECL乐观调整因子Z
     */
    private String rzy_ecl_opt_z;

    /**
     * ECL悲观调整因子Z
     */
    private String rzy_ecl_glo_z;

    /**
     * 下迁等级
     **/
    private int rzy_ecl_down_level;

    /**
     * 违约损失率
     **/
    private String lgd;

    private BigDecimal base_weight;
    private BigDecimal opt_weight;
    private BigDecimal glo_weight;

    /**
     * 日期差合同到期日-当前/预计日期 最小0
     **/
    private Long date_difference;

    //上迁结果
    private Integer promotionResult;

}
