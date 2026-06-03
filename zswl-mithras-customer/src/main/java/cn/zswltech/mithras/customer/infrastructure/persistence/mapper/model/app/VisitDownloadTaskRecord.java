package cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.app;

import cn.zswltech.mithras.service.mapper.model.BaseModelWithLogicDelete;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * @author dingqi
 * @date 2025/9/18
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("visit_download_task_record")
public class VisitDownloadTaskRecord extends BaseModelWithLogicDelete {
    @TableId(type = IdType.AUTO)
    @TableField(value = "id")
    private Long id;

    @TableField(value = "user_id")
    private Long userId;

    @TableField(value = "req_params")
    private String reqParams;

    @TableField(value = "task_status")
    private String taskStatus;

    @TableField(value = "finish_time")
    private LocalDateTime finishTime;

    @TableField(value = "file_path")
    private String filePath;

    @TableField(value = "file_name")
    private String fileName;

    @TableField(value = "remark")
    private String remark;
}
