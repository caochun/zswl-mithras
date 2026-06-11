package cn.zswltech.mithras.archives.mapper.model;

import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 
 * @TableName archives_download_permission
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value ="archives_download_permission")
@Data
public class ArchivesDownloadPermission extends BaseModel implements Serializable {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 文件id
     */
    @TableField(value = "materials_id")
    private Long materialsId;

    /**
     * 用户id
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 0 正在审批 1 审批通过 2 审批拒绝
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 申请原因
     */
    @TableField(value = "reason_id")
    private Long reasonId;

    /**
     * 过期时间
     */
    @TableField(value = "expires")
    private LocalDateTime expires;

    /**
     * 审批批次
     */
    @TableField(value = "batch")
    private String batch;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
