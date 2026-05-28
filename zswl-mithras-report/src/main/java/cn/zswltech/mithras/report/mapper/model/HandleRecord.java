package cn.zswltech.mithras.report.mapper.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 征信报送各模块处理时间
 *
 * @author wangchuanhao
 * @date 2022/10/8 1:34 PM
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class HandleRecord {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 模块枚举
     */
    @TableField("module")
    private String module;

    /**
     * 处理时间
     */
    @TableField("deal_time")
    private LocalDateTime dealTime;

}
