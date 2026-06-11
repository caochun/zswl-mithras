package cn.zswltech.mithras.message.mapper.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @description: 邮件发送失败日志表
 * @author: huangping
 * @date: 2025/11/20  11:39
 * @version: 1.0
 */
@Data
@TableName("email_send_fail_log")
public class EmailSendFailLog {
    @TableId(type = IdType.AUTO)
    private Long id;                  // 主键ID
    private String toSet;             // 接收人邮箱（逗号分隔）
    private String ccSet;             // 抄送人邮箱（逗号分隔）
    private String bccSet;            // 密送人邮箱（逗号分隔）
    private String emailType;         // 邮件类型（新增字段，由子类指定）
    private String emailTitle;        // 邮件标题
    private String businessData;      // 业务数据（JSON格式）
    private String errorMsg;          // 失败异常信息
    private Long createBy;            // 创建人ID（新增字段）
    private LocalDateTime createTime; // 创建时间（自动填充）
    private Long updateBy;            // 最后更新人ID（新增字段）
    private LocalDateTime updateTime; // 更新时间（自动填充）
    private Integer retryTimes;       // 重试次数（默认0）
    private Integer retryStatus;      // 重试状态（0=未重试，1=已重试，2=重试失败）
}
