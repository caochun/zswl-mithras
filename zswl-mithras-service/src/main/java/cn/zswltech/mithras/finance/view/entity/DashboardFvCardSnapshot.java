package cn.zswltech.mithras.finance.view.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 工作台融资视图卡片快照信息表(DashboardFvCardSnapshot)表实体类
 *
 * @author makejava
 * @since 2025-09-02 09:30:34
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dashboard_fv_card_snapshot")
public class DashboardFvCardSnapshot extends Model<DashboardFvCardSnapshot> {
    /**
     * Id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 数据时点
     */
    @TableField("data_time")
    private LocalDate dataTime;

    /**
     * 卡片编码
     */
    @TableField("card_code")
    private String cardCode;

    /**
     * 卡片名称
     */
    @TableField("card_name")
    private String cardName;

    /**
     * 卡片数据
     */
    @TableField("card_data")
    private String cardData;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 获取主键值
     *
     * @return 主键值
     */
    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}

