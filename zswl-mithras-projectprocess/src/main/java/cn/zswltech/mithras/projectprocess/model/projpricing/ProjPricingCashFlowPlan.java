package cn.zswltech.mithras.projectprocess.model.projpricing;

import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import cn.zswltech.mithras.foundation.persistence.tag.IEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDate;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 项目定价-现金流计划表
 * </p>
 *
 * @author chenyifei
 * @since 2024-08-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("proj_pricing_cash_flow_plan")
public class ProjPricingCashFlowPlan extends BaseModel implements Serializable, IEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 现金流量明细表id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 所属项目定价记录ID
     */
    @TableField("project_id")
    private Long projectId;

    /**
     * 日期
     */
    @TableField("cash_flow_date")
    private LocalDate cashFlowDate;

    /**
     * 期项
     */
    @TableField("cash_flow_phase")
    private Integer cashFlowPhase;

    /**
     * 现金流金额
     */
    @TableField("cash_flow_amount")
    private Long cashFlowAmount;

    /**
     * 租金
     */
    @TableField("rent")
    private Long rent;

    /**
     * 本金
     */
    @TableField("principal")
    private Long principal;

    /**
     * 利息
     */
    @TableField("interest")
    private Long interest;

    /**
     * 剩余本金
     */
    @TableField("remaining_principal")
    private Long remainingPrincipal;

    /**
     * 创建人id、发起人id	
     */
    @TableField("create_by")
    private Long createBy;

    /**
     * 创建时间。默认当前时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 最后更新人id	
     */
    @TableField("update_by")
    private Long updateBy;

    /**
     * 更新时间；每次记录变化，自动更新为当前时间	
     */
    @TableField("update_time")
    private LocalDateTime updateTime;


    @Override
    public void setMainId(Long id) {
        setId(id);
    }

    @Override
    public Long getMainId() {
        return getId();
    }
}
