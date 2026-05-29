package cn.zswltech.mithras.service.service.newftp.model;

import cn.zswltech.mithras.common.model.BaseModelWithLogicDelete;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author dingqi
 * @date 2025/3/4
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("new_ftp_change_apply_record")
public class NewFtpChangeApplyRecord extends BaseModelWithLogicDelete {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    @TableField(value = "id")
    private Long id;

    /**
     * 申请人id
     */
    @TableField(value = "apply_user_id")
    private Long applyUserId;

    /**
     * 审批状态
     */
    @TableField(value = "approval_status")
    private String approvalStatus;
}
