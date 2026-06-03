package cn.zswltech.mithras.customer.authorityrecord.infrastructure.model;

import cn.zswltech.mithras.service.mapper.model.BaseModelWithLogicDelete;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author dingqi
 * @date 2024/9/13
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("client_authority_apply_record")
public class ClientAuthorityApplyRecord extends BaseModelWithLogicDelete {
    @TableId(type = IdType.AUTO)
    @TableField(value = "id")
    private Long id;

    @TableField(value = "batch_no")
    private String batchNo;

    @TableField(value = "client_id")
    private Long clientId;

    @TableField(value = "dept_id")
    private Long deptId;

    @TableField(value = "user_id")
    private Long userId;

    @TableField(value = "level")
    private Integer level;

    @TableField(value = "reason")
    private String reason;

    @TableField(value = "process_instance_id")
    private String processInstanceId;
}
