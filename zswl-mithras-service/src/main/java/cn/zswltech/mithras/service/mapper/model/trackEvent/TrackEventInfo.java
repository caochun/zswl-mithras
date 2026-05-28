package cn.zswltech.mithras.service.mapper.model.trackEvent;

import cn.zswltech.mithras.service.mapper.model.BaseModel;
import cn.zswltech.mithras.service.mapper.tag.IEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@TableName("track_event_info")
public class TrackEventInfo extends BaseModel implements Serializable, IEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 业务id
     */
    @TableField("biz_id")
    private Long bizId;

    /**
     * 业务来源
     */
    @TableField("biz_source")
    private String bizSource;

    /**
     * 任务名称
     */
    @TableField("task_name")
    private String taskName;

    /**
     * 任务类型 枚举-TrackTaskTypeEnum
     */
    @TableField("task_type")
    private String taskType;

    /**
     * 计划日期
     */
    @TableField("plan_time")
    private LocalDate planTime;

    /**
     * 起租后X自然日
     */
    @TableField("start_rent_after_day")
    private Integer startRentAfterDay;

    /**
     * 处理人
     */
    @TableField("processor_id")
    private Long processorId;

    /**
     * 处理人岗位
     */
    @TableField("processor_dept")
    private String processorDept;

    /**
     * 提醒频率 枚举-TrackFrequencyEnum
     */
    @TableField("remind_frequency")
    private String remindFrequency;

    /**
     * 任务内容
     */
    @TableField("task_content")
    private String taskContent;

    /**
     * 任务状态
     */
    @TableField("task_status")
    private Boolean taskStatus;

    /**
     * 是否从台账新增
     */
    @TableField("is_ledger")
    private Boolean isLedger;


    @TableField("contract_code")
    private String contractCode;

    @TableField("client_id")
    private Long clientId;

    @TableField("proj_name")
    private String projName;

    @TableField("proj_code")
    private String projCode;

    @TableField("on_flow_count")
    private int onFlowCount;

    //项目评审会议纪要ID
    @TableField(value = "proj_review_meet_minute_id")
    private Long projReviewMeetMinuteId;

    @Override
    public void setMainId(Long id) {
        this.bizId = id;
    }

    @Override
    public Long getMainId() {
        return bizId;
    }
}
