package cn.zswltech.mithras.service.mapper.model.dashboard;

import cn.zswltech.mithras.service.mapper.model.BaseModelWithLogicDelete;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 工作台-人力调整明细表
 * </p>
 *
 * @author chenyifei
 * @since 2024-07-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("dashboard_adjust_person_info")
public class DashboardAdjustPersonInfo extends BaseModelWithLogicDelete implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 部门id
     */
    @TableField("dept_id")
    private Long deptId;

    @TableField("dept_name")
    private String deptName;

    /**
     * 职位
     */
    @TableField("position")
    private String position;

    /**
     * 项目经理id
     */
    @TableField("proj_manager_id")
    private Long projManagerId;

    /**
     * 项目经理名称
     */
    @TableField("proj_manager_name")
    private String projManagerName;

    /**
     * 业务组分类
     */
    @TableField("business_group")
    private String businessGroup;

    /**
     * 批次号
     */
    @TableField("batch_number")
    private Long batchNumber;



}
