package cn.zswltech.mithras.kpi.mapper.model;

import cn.zswltech.mithras.foundation.persistence.model.BaseModelWithLogicDelete;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @description 资产减值记录表
 * @author vico
 * @date 2025-09-28
 */
@Data
public class EclExecuteRecord extends BaseModelWithLogicDelete implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * id
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
    * 调用记录编号
    */
    @TableField("model_record_key")
    private String modelRecordKey;

    /**
    * 拨备计提详情id
    */
    @TableField("kpi_provision_detail_id")
    private Long kpiProvisionDetailId;

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

    /**
    * 内评评级
    */
    @TableField("inner_md_level")
    private String innerMdLevel;

    /**
    * ecl违约概率
    */
    @TableField("ecl_pd")
    private BigDecimal eclPd;

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
    @TableField(value = "`group`")
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
    private Long lateDay;

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

    @TableField("receipt_id")
    private Long receiptId;

    @TableField("receipt_code")
    private String receiptCode;

    /**
    * 债项阶段,原始
    */
    @TableField("ecl_step")
    private Integer eclStep;

    //上迁结果
    @TableField("promotion_result")
    private Integer promotionResult;

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

    /**
     * 下迁等级
     **/
    @TableField("rzy_ecl_down_level")
    private Integer rzyEclDownLevel;

    /**
     * 来源类型 0自动，1手工添加
     **/
    @TableField("source_type")
    private Integer sourceType;

}
