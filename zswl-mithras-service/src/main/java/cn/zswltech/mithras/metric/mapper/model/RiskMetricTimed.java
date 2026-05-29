package cn.zswltech.mithras.metric.mapper.model;

import cn.zswltech.mithras.common.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @author luyi
 * @description risk_metric_timed
 * @date 2022-12-19
 */
@Data
public class RiskMetricTimed extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 数据时点
     */
    @TableField("data_time")
    private LocalDate dataTime;

    /**
     * renting_proj_code
     */
    @TableField("renting_proj_code")
    private String rentingProjCode;

    @TableField("proj_name")
    private String projName;

    /**
     * proj_type
     */
    @TableField("proj_type")
    private String projType;

    /**
     * level_5_type
     */
    @TableField("level_5_type")
    private String level5Type;

    /**
     * client_id
     */
    @TableField("client_id")
    private Long clientId;

    @TableField("client_name")
    private String clientName;

    @TableField("industry_type")
    private String industryType;

    @TableField("industry_type_name")
    private String industryTypeName;
    /**
     * belong_group_id
     */
    @TableField("belong_group_id")
    private Long belongGroupId;

    @TableField("belong_group_name")
    private String belongGroupName;

    /**
     * related
     */
    @TableField("related")
    private Boolean related;

    /**
     * 剩余本金
     */
    @TableField("left_capital")
    private Long leftCapital;

    /**
     * 逾期金额（本金利息）
     */
    @TableField("overdue_total")
    private Long overdueTotal;

}
