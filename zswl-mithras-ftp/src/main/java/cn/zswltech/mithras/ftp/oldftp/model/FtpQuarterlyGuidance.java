package cn.zswltech.mithras.ftp.oldftp.model;
import cn.zswltech.mithras.foundation.enums.common.RecordStatus;
import cn.zswltech.mithras.foundation.persistence.tag.IEntity;
import cn.zswltech.mithras.ftp.oldftp.enums.FtpProcessStatus;
import cn.zswltech.mithras.foundation.state.IStateMachineEntity;
import cn.zswltech.mithras.foundation.state.ProcessStatus;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import cn.zswltech.mithras.foundation.persistence.model.BaseModel;

/**
 * @description 季度指导
 * @author zhaozhengkang
 * @date 2023-01-09
 */
@Data
@TableName("ftp_quarterly_guidance")
public class FtpQuarterlyGuidance extends BaseModel implements Serializable, IEntity, IStateMachineEntity {

    private static final long serialVersionUID = 1L;

    /**
    * 主键
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
    * 年度
    */
    @TableField("year")
    private Integer year;

    /**
    * 季度
    */
    @TableField("quarter")
    private Integer quarter;

    /**
    * 审批状态
    */
    @TableField("guidance_process_status")
    private String guidanceProcessStatus;

    @TableField("guidance_record_status")
    private String guidanceRecordStatus;

    @Override
    public void setMainId(Long id) {
        this.id = id;
    }

    @Override
    public Long getMainId() {
        return this.id;
    }

    @Override
    public void setProcessStatus(ProcessStatus processState) {
        this.guidanceProcessStatus = processState.name();
    }

    @Override
    public ProcessStatus getProcessStatus() {
        return FtpProcessStatus.of(guidanceProcessStatus);
    }

    @Override
    public RecordStatus getRecordStatus() {
        return RecordStatus.of(guidanceRecordStatus);
    }

    @Override
    public void setRecordStatus(RecordStatus recordStatus) {
        this.guidanceRecordStatus = recordStatus.name();
    }

    @Override
    public String getProcessStatusFieldName() {
        return "guidance_process_status";
    }

    @Override
    public String getRecordStatusFieldName() {
        return "guidance_record_status";
    }
}
