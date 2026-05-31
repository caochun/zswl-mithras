package cn.zswltech.mithras.service.mapper.model;

import cn.zswltech.mithras.service.annotation.NotCompareColumn;
import cn.zswltech.mithras.service.plugin.AutoAuditEntity;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * @author junke
 */
@Data
@AutoAuditEntity
public class BaseModel {

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotCompareColumn
    @TableField(value = "create_time", updateStrategy = FieldStrategy.NEVER)
    private LocalDateTime createTime;
    @NotCompareColumn
    @TableField(value = "create_by", updateStrategy = FieldStrategy.NEVER)
    private Long createBy;
    @NotCompareColumn
    @TableField(value = "update_time", updateStrategy = FieldStrategy.NEVER)
    private LocalDateTime updateTime;
    @NotCompareColumn
    @TableField("update_by")
    private Long updateBy;

    public void reset() {
        this.createBy = null;
        this.createTime = null;
        this.updateBy = null;
        this.updateTime = null;
    }
}
