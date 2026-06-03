package cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client;

import cn.zswltech.mithras.service.mapper.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/7/25 09:47
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("proj_client_role")
public class ProjClientRole extends BaseModel {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    @TableField("module_type")
    private String moduleType;
    @TableField("main_id")
    private Long mainId;
    @TableField("client_id")
    private Long clientId;
    @TableField("role")
    private String role;
}
