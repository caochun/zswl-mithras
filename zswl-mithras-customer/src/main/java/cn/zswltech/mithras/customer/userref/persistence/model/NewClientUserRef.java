package cn.zswltech.mithras.customer.userref.persistence.model;

import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author dingqi
 * @date 2024/9/11
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("new_client_user_ref")
public class NewClientUserRef extends BaseModel {
    @TableId(type = IdType.AUTO)
    @TableField(value = "id")
    private Long id;

    @TableField(value = "client_id")
    private Long clientId;

    @TableField(value = "user_id")
    private Long userId;
}
