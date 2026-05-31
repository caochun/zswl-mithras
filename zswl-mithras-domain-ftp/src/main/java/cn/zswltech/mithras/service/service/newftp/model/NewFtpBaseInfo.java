package cn.zswltech.mithras.service.service.newftp.model;

import cn.zswltech.mithras.service.enums.common.RecordStatus;
import cn.zswltech.mithras.service.enums.ftp.FtpBusinessVersion;
import cn.zswltech.mithras.service.enums.newftp.NewFtpProcessStatus;
import cn.zswltech.mithras.service.enums.newftp.PricingFrequencyEnum;
import cn.zswltech.mithras.service.mapper.model.BaseModel;
import cn.zswltech.mithras.service.mapper.tag.IEntity;
import cn.zswltech.mithras.service.service.projfms.IStateMachineEntity;
import cn.zswltech.mithras.service.service.projfms.ProcessStatus;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author zhaozhengkang
 * @description ftp主表
 * @date 2023-05-21
 */
@Data
public class NewFtpBaseInfo extends BaseModel implements IEntity, Serializable, IStateMachineEntity {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 定价频率
     * {@link PricingFrequencyEnum#name()}
     **/
    @TableField("pricing_frequency")
    private String pricingFrequency;

    /**
     * 所属月份
     */
    @TableField("month")
    private LocalDate month;
    /**
     * ftp流程状态
     */
    @TableField("ftp_process_status")
    private String ftpProcessStatus;
    /**
     * ftp记录状态
     */
    @TableField("ftp_record_status")
    private String ftpRecordStatus;

    @TableField("effect_time")
    private LocalDateTime effectTime;

    /**
     * 最新版本号
     */
    @TableField("newest_version")
    private String newestVersion;

    /**
     * FTP报价计算标志, 默认0, 0: 未计算过 1: 计算过
     */
    @TableField(value = "calculate_deduction_flag")
    private Integer calculateDeductionFlag;

    /**
     * FTP计价指导计算标志, 默认0, 0: 未计算过 1: 计算过
     */
    @TableField(value = "calculate_guidance_flag")
    private Integer calculateGuidanceFlag;

    /**
     * 业务版本 {@link FtpBusinessVersion#name()}
     */
    @TableField(value = "ftp_business_version")
    private String ftpBusinessVersion;

    @Override
    public ProcessStatus getProcessStatus() {
        return NewFtpProcessStatus.valueOf(ftpProcessStatus);
    }

    @Override
    public void setProcessStatus(ProcessStatus processState) {
        this.ftpProcessStatus = processState.name();
    }

    @Override
    public RecordStatus getRecordStatus() {
        return RecordStatus.valueOf(ftpRecordStatus);
    }

    @Override
    public void setRecordStatus(RecordStatus recordStatus) {
        this.ftpRecordStatus = recordStatus.name();
    }

    @Override
    public String getProcessStatusFieldName() {
        return "ftp_process_status";
    }

    @Override
    public String getRecordStatusFieldName() {
        return "ftp_record_status";
    }

    @Override
    public void setMainId(Long id) {
        this.id = id;
    }

    @Override
    public Long getMainId() {
        return id;
    }
}
