package cn.zswltech.mithras.customer.mapper.model.client;

import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author dingqi
 * @date 2024/9/9
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("client_create_record")
public class ClientCreateRecord extends BaseModel {
    @TableId(type = IdType.AUTO)
    @TableField(value = "id")
    private Long id;

    @TableField(value = "client_id")
    private Long clientId;

    @TableField(value = "user_id")
    private Long userId;

    @TableField(value = "dept_id")
    private Long deptId;

    @TableField(value = "unique_code")
    private String uniqueCode;
}
