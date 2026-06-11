package cn.zswltech.mithras.dashboard.mapper.model;

import cn.zswltech.mithras.foundation.persistence.model.BaseModelWithLogicDelete;
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
 * 工作台-拜访明细表
 * </p>
 *
 * @author chenyifei
 * @since 2024-07-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("dashboard_visit_info")
public class DashboardVisitInfo extends BaseModelWithLogicDelete implements Serializable {

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
     * 拜访日期
     */
    @TableField("visit_time")
    private LocalDateTime visitTime;

    /**
     * 拜访客户数
     */
    @TableField("visit_client_count")
    private Integer visitClientCount;



}
