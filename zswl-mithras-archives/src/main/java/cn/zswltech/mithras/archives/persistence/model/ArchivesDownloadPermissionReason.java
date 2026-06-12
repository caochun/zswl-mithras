package cn.zswltech.mithras.archives.persistence.model;

import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 
 * @TableName archives_download_permission_reason
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value ="archives_download_permission_reason")
@Data
public class ArchivesDownloadPermissionReason extends BaseModel implements Serializable {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 申请原因
     */
    @TableField(value = "reason")
    private String reason;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
