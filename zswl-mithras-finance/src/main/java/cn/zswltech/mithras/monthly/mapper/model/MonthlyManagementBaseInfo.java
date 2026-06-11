package cn.zswltech.mithras.monthly.mapper.model;

import cn.zswltech.mithras.monthly.enums.MonthlyManagementStatusEnum;
import cn.zswltech.mithras.foundation.persistence.model.BaseModelWithLogicDelete;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 月结管理主表
 * @author yangxiong
 * @TableName monthly_management_base_info
 */
@TableName(value ="monthly_management_base_info")
@Data
@Accessors(chain = true)
public class MonthlyManagementBaseInfo extends BaseModelWithLogicDelete implements Serializable {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField(value = "main_id")
    private Long mainId;

    /**
     * 年份
     */
    @TableField(value = "year")
    private Integer year;

    /**
     * 月份
     */
    @TableField(value = "month")
    private Integer month;

    /**
     * 实际利率法(含税)
     */
    @TableField(value = "air_count")
    private Long airCount;

    /**
     * 实际利率法(不含税)
     */
    @TableField(value = "air_count_exclude_tax")
    private Long airCountExcludeTax;

    /**
     * 剩余本金法(含税)
     */
    @TableField(value = "rp_count")
    private Long rpCount;

    /**
     * 剩余本金法(不含税)
     */
    @TableField(value = "rp_count_exclude_tax")
    private Long rpCountExcludeTax;

    /**
     * 当期计提成本(含税)
     */
    @TableField(value = "cost_count")
    private Long costCount;

    /**
     * 当期计提成本(不含税)
     */
    @TableField(value = "cost_count_exclude_tax")
    private Long costCountExcludeTax;

    /**
     * 印花税
     */
    @TableField(value = "stamp_duty_count")
    private Long stampDutyCount;

    /**
     * 确认日期
     */
    @TableField(value = "confirm_date")
    private LocalDate confirmDate;

    /**
     * 状态 {@link MonthlyManagementStatusEnum}
     */
    @TableField(value = "status")
    private String status;

    /**
     * 关账日期
     */
    @TableField(value = "close_date")
    private LocalDateTime closeDate;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}