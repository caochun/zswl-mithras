package cn.zswltech.mithras.budget.mapper.model;

import cn.zswltech.mithras.foundation.persistence.model.BaseModelWithLogicDelete;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @description 资产减值预测详情记录表
 * @author vico
 * @date 2025-10-14
 */
@Data
public class EclExecutePredictRecord extends BaseModelWithLogicDelete implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * id
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
    * 预测计划id
    */
    @TableField("execute_predict_id")
    private Long executePredictId;

    /**
    * 拨备预测记录表id
    */
    @TableField("budget_plan_profit_detail_id")
    private Long budgetPlanProfitDetailId;

    /**
    * 拨备计提详情id
    */
    @TableField("kpi_provision_detail_id")
    private Long kpiProvisionDetailId;

    /**
     * 调用记录编号
     */
    @TableField("model_record_key")
    private String modelRecordKey;

    /**
     * 计算月份
     */
    @TableField("calculation_date")
    private LocalDate calculationDate;

    /**
     * 是否逾期计算 0 非逾期计算 ,1逾期计算
     */
    @TableField("overdue_flag")
    private Integer overdueFlag;

    /**
    * 客户id
    */
    @TableField("client_id")
    private Long clientId;

    /**
    * 客户名称
    */
    @TableField("client_name")
    private String clientName;

    /**
     * 评估主体ID
     **/
    @TableField("evaluation_subject_id")
    private Long evaluationSubjectId;

    /**
     * 评估主体ID
     **/
    @TableField("evaluation_subject_name")
    private String evaluationSubjectName;

    /**
    * 合同id
    */
    @TableField("contract_id")
    private Long contractId;

    /**
    * 合同编号
    */
    @TableField("contract_code")
    private String contractCode;

    @TableField("receipt_id")
    private Long receiptId;

    @TableField("receipt_code")
    private String receiptCode;


    /**
    * 内评评级
    */
    @TableField("inner_md_level")
    private String innerMdLevel;

    /**
    * ecl违约概率
    */
    @TableField("ecl_pd")
    private String eclPd;

    /**
    * 外评级别
    */
    @TableField("outer_level")
    private String outerLevel;

    /**
    * ecl外评违约概率
    */
    @TableField("ecl_outer_pd")
    private String eclOuterPd;

    /**
    * 所属分组
    */
    @TableField("`group`")
    private String group;

    /**
    * 五级分类
    */
    @TableField("classify")
    private String classify;

    /**
    * 逾期天数
    */
    @TableField("late_day")
    private Integer lateDay;

    /**
     * 最早逾期日期
     */
    @TableField("late_date")
    private LocalDate lateDate;

    /**
    * 租赁物类型
    */
    @TableField("lease_type")
    private String leaseType;

    /**
    * 剩余本金
    */
    @TableField("remain_principal")
    private BigDecimal remainPrincipal;

    /**
    * 应计利息
    */
    @TableField("accrued_interest")
    private BigDecimal accruedInterest;

    /**
    * 保证金
    */
    @TableField("deposit")
    private BigDecimal deposit;

    /**
    * 下一期租金
    */
    @TableField("next_rent")
    private BigDecimal nextRent;

    /**
    * 风险敞口
    */
    @TableField("risk_exposure")
    private BigDecimal riskExposure;

    /**
    * ead计算值，融资租赁：max（剩余本金应计利息-剩余保证金，0）经营性租赁：max（拨备计提月份下一期租金，0）
    */
    @TableField("ead")
    private BigDecimal ead;

    /**
    * 合同到期日
    */
    @TableField("contract_expiration_date")
    private LocalDate contractExpirationDate;

    /**
    * 债项阶段
    */
    @TableField("ecl_step")
    private String eclStep;

    /**
     * 债项阶段,上迁后结果
     **/
    @TableField("promotion_result")
    private Integer promotionResult;

    /**
     * 债项阶段,上迁后结果
     **/
    @TableField("promotion_result_handle")
    private Integer promotionResultHandle;

    /**
    * 期限调整系数t
    */
    @TableField("ecl_factor_t")
    private BigDecimal eclFactorT;

    /**
    * ecl基准/乐观/悲观调整因子z
    */
    @TableField("ecl_param_z")
    private String eclParamZ;

    /**
    * ecl基准/乐观/悲观情景权重
    */
    @TableField("ecl_param_weight")
    private String eclParamWeight;

    /**
    * 违约损失率(lgd)
    */
    @TableField("lgd")
    private String lgd;

    /**
    * 下迁等级
    */
    @TableField("rzy_ecl_down_level")
    private Integer rzyEclDownLevel;

    /**
    * 基准pdforward
    */
    @TableField("base_pd_forward")
    private BigDecimal basePdForward;

    /**
    * 乐观pdforward
    */
    @TableField("opt_pd_forward")
    private BigDecimal optPdForward;

    /**
    * 悲观pdforward
    */
    @TableField("glo_pd_forward")
    private BigDecimal gloPdForward;

    /**
    * 基准pdifrs9
    */
    @TableField("ecl_base_ifrs9")
    private BigDecimal eclBaseIfrs9;

    /**
    * 乐观pdifrs9
    */
    @TableField("ecl_opt_ifrs9")
    private BigDecimal eclOptIfrs9;

    /**
    * 悲观pdifrs9
    */
    @TableField("ecl_glo_ifrs9")
    private BigDecimal eclGloIfrs9;

    /**
    * 基准ecl
    */
    @TableField("base_ecl")
    private BigDecimal baseEcl;

    /**
    * 乐观ecl
    */
    @TableField("opt_ecl")
    private BigDecimal optEcl;

    /**
    * 悲观ecl
    */
    @TableField("glo_ecl")
    private BigDecimal gloEcl;

    /**
    * ecl
    */
    @TableField("ecl")
    private BigDecimal ecl;

    /**
    * 备注
    */
    @TableField("remark")
    private String remark;

    @TableField("calculation_type")
    private String calculationType;


    /**
     * 模型调用时间
     */
    @TableField("calculation_model_time")
    private LocalDateTime calculationModelTime;

    /**
     * 来源类型 0自动，1手工添加
     **/
    @TableField("source_type")
    private Integer sourceType;

}
