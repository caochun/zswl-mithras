package cn.zswltech.mithras.service.mapper.model.workbench;

import cn.zswltech.mithras.common.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/6/9 14:17
 */
@Data
public class WorkbenchCardUserRef extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 指标名称
     */
    @TableField("metric_name")
    private String metricName;

    @TableField("user_id")
    private Long userId;
}
