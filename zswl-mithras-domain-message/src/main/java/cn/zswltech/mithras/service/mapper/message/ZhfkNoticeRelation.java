package cn.zswltech.mithras.service.mapper.message;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @ClassName zhfkNoticeRelation
 * @Description
 * @Author jackerhe
 * @Date 2022/9/16 4:22 下午
 * @Version 1.0
 **/
@Data
public class ZhfkNoticeRelation implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("mithras_id")
    private String mithrasId;

    @TableField("message_id")
    private Long messageId;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;

    /**
     * 操作记录 [办理状态RemoteIsRemarkEnum， 读取状态 0未读，1已读]
     **/
    @TableField("operation_records")
    private String operationRecords;

}
