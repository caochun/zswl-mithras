package cn.zswltech.mithras.kpi.model;

import cn.zswltech.mithras.kpi.enums.KpiProjectClassifyEnum;
import cn.zswltech.mithras.kpi.enums.KpiProjectSourceDistributionEnum;
import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import cn.zswltech.mithras.foundation.persistence.tag.IEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2023/6/14
 * @description 绩效考核-项目分配表（新）-基本信息
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("kpi_project_distribution_base_info")
public class KpiProjectDistributionBaseInfo extends BaseModel implements IEntity {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    @TableField(value = "id")
    private Long id;

    /**
     * 项目分配id
     */
    @TableField(value = "project_distribution_id")
    private Long projectDistributionId;

    /**
     * 合同编号
     */
    @TableField(value = "contract_code")
    private String contractCode;

    /**
     * 项目名称
     */
    @TableField(value = "proj_name")
    private String projName;

    /**
     * 项目类别 {@link KpiProjectClassifyEnum#name()}
     */
    @TableField(value = "proj_classify")
    private String projClassify;

    /**
     * 项目来源 {@link KpiProjectSourceDistributionEnum#name()}
     */
    @TableField(value = "proj_source")
    private String projSource;

    /**
     * 合同开始时间
     */
    @TableField(value = "contract_start_date")
    private LocalDate contractStartDate;

    /**
     * 合同结束时间
     */
    @TableField(value = "contract_end_date")
    private LocalDate contractEndDate;

    /**
     * 利润所属部门id
     */
    @TableField(value = "profit_belong_dept_id")
    private Long profitBelongDeptId;

    /**
     * 团队长用户id
     */
    @TableField(value = "team_leader_id")
    private Long teamLeaderId;

    /**
     * 生效年份
     */
    @TableField(value = "effect_year")
    private Integer effectYear;

    /**
     * 生效月份
     */
    @TableField(value = "effect_month")
    private Integer effectMonth;

    /**
     * 项目交接备注
     */
    @TableField(value = "remark")
    private String remark;

    /**
     * 变更原因
     */
    @TableField(value = "change_reason")
    private String changeReason;

    /**
     * 说明
     **/
    @TableField(value = "supple_describe")
    private String suppleDescribe;

    @Override
    public void setMainId(Long id) {
        this.id = projectDistributionId;
    }

    @Override
    public Long getMainId() {
        return this.projectDistributionId;
    }
}
