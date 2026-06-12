package cn.zswltech.mithras.workflow.persistence.model.attention;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 流程关注记录
 *
 * @author wangchuanhao
 * @date 2023/4/10 12:39 PM
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProcAttentionRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 流程id
     */
    @TableField("process_instance_id")
    private String processInstanceId;

    /**
     * 用户id
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 0不关注，1关注
     */
    @TableField("attention_type")
    private Integer attentionType;

    @TableField(value = "create_time", updateStrategy = FieldStrategy.NEVER)
    private LocalDateTime createTime;

    @TableField(value = "update_time", updateStrategy = FieldStrategy.NEVER)
    private LocalDateTime updateTime;


}
