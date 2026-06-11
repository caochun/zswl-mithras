package cn.zswltech.mithras.kpi.model;

import cn.zswltech.mithras.foundation.persistence.model.BaseModelWithLogicDelete;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 业绩信息详情表
 * @author yangxiong
 * @TableName performance_record_info
 */
@TableName(value ="performance_record_info")
@Data
public class PerformanceRecordInfo extends BaseModelWithLogicDelete implements Serializable {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 业绩信息基本表ID
     */
    @TableField(value = "performance_id")
    private Long performanceId;

    /**
     * 月份
     */
    @TableField(value = "month")
    private Integer month;

    /**
     * 目标金额
     */
    @TableField(value = "target_amount")
    private Long targetAmount;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}