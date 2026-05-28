package cn.zswltech.mithras.service.mapper.model.client;

import cn.zswltech.mithras.service.mapper.model.BaseModelWithLogicDelete;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author zhouning
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "client_authority")
public class ClientAuthority extends BaseModelWithLogicDelete {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("client_id")
    private Long clientId;

    @TableField("dept_id")
    private Long deptId;

    @TableField("user_id")
    private Long userId;

    @TableField("level")
    private Integer level;

    @TableField("source_business_type")
    private String sourceBusinessType;

    @TableField("source_id")
    private String sourceId;
}
