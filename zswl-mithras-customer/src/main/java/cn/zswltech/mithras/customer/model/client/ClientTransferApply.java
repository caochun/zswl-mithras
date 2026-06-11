package cn.zswltech.mithras.customer.model.client;

import cn.zswltech.mithras.foundation.persistence.model.BaseModelWithLogicDelete;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author dingqi
 * @date 2024/9/26
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("client_transfer_apply")
public class ClientTransferApply extends BaseModelWithLogicDelete {
    @TableId(type = IdType.AUTO)
    @TableField(value = "id")
    private Long id;

    @TableField(value = "batch_no")
    private String batchNo;

    @TableField(value = "approval_status")
    private String approvalStatus;
}
