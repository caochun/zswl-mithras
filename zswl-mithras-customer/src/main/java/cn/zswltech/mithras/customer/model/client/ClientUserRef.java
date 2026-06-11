package cn.zswltech.mithras.customer.model.client;

import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * @author dingqi
 * @date 2024/9/11
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("client_user_ref")
public class ClientUserRef extends BaseModel {
    @TableId(type = IdType.AUTO)
    @TableField(value = "id")
    private Long id;

    @TableField(value = "client_id")
    private Long clientId;

    @TableField(value = "user_id")
    private Long userId;

    @TableField(value = "dept_id")
    private Long deptId;

    @TableField(value = "last_operate_time")
    private LocalDateTime lastOperateTime;

    @TableField(value = "last_operate_type")
    private String lastOperateType;

    public enum OperateTypeEnum {
        INIT, READ, WRITE
    }
}
