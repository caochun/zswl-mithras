package cn.zswltech.mithras.system.mapper.model;

import cn.zswltech.mithras.service.mapper.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author dingqi
 * @date 2024/9/10
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("system_user_operate_log")
public class SystemUserOperateLog extends BaseModel {
    @TableId(type = IdType.AUTO)
    @TableField(value = "id")
    private Long id;

    @TableField(value = "user_id")
    private Long userId;

    @TableField(value = "user_name")
    private String userName;

    @TableField(value = "url")
    private String url;

    @TableField(value = "req_data")
    private String reqData;
}
