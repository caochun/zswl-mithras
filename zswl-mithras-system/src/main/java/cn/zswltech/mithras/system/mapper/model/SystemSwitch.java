package cn.zswltech.mithras.system.mapper.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author yibin
 */
@Data
@TableName("system_switch")
public class SystemSwitch {
    @TableId("id")
    private Long id;
    @TableField("code")
    private String code;
    @TableField("description")
    private String description;
    @TableField("value")
    private Integer value;
    @TableField("create_time")
    private LocalDateTime createTime;
}
