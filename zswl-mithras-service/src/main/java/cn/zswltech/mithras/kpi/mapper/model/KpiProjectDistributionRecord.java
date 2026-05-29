package cn.zswltech.mithras.kpi.mapper.model;

import cn.zswltech.mithras.service.enums.kpi.KpiProjectClassifyEnum;
import cn.zswltech.mithras.service.enums.kpi.KpiProjectSourceDistributionEnum;
import cn.zswltech.mithras.common.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @description 绩效考核-项目分配记录表
 * @author vico
 * @date 2024-09-27
 */
@Data
public class KpiProjectDistributionRecord extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * id
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
    * 批次号
    */
    @TableField("batch_number")
    private Integer batchNumber;

    @TableField("calculate_date")
    private LocalDate calculateDate;

    @TableField("project_distribution_id")
    private Long projectDistributionId;

    @TableField("kpi_project_distribution_version")
    private String kpiProjectDistributionVersion;

    /**
    * 合同id
    */
    @TableField("contract_id")
    private Long contractId;

    /**
    * 分配状态，0-未分配，1-已分配
    */
    @TableField("distribution_status")
    private Integer distributionStatus;

    /**
    * 审批状态
    */
    @TableField("approval_status")
    private String approvalStatus;

    /**
     * 项目类别 {@link KpiProjectClassifyEnum#name()}
     */
    @TableField("proj_classify")
    private String projClassify;

    /**
     * 项目来源 {@link KpiProjectSourceDistributionEnum#name()}
     */
    @TableField("proj_source")
    private String projSource;

    /**
    * 合同开始时间
    */
    @TableField("contract_start_date")
    private LocalDate contractStartDate;

    /**
    * 合同结束时间
    */
    @TableField("contract_end_date")
    private LocalDate contractEndDate;

    /**
    * 利润所属部门id
    */
    @TableField("profit_belong_dept_id")
    private Long profitBelongDeptId;

    /**
    * 团队长用户id
    */
    @TableField("team_leader_id")
    private Long teamLeaderId;

    /**
    * 生效年份
    */
    @TableField("effect_year")
    private Integer effectYear;

    /**
    * 生效月份
    */
    @TableField("effect_month")
    private Integer effectMonth;

}
