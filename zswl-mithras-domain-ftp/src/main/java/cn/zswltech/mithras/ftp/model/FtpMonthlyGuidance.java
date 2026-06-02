package cn.zswltech.mithras.ftp.model;
import cn.zswltech.mithras.service.enums.common.RecordStatus;
import cn.zswltech.mithras.service.mapper.tag.IEntity;
import cn.zswltech.mithras.ftp.enums.FtpProcessStatus;
import cn.zswltech.mithras.service.service.projfms.IStateMachineEntity;
import cn.zswltech.mithras.service.service.projfms.ProcessStatus;
import lombok.Data;

import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import cn.zswltech.mithras.service.mapper.model.BaseModel;

/**
 * @description 月度指导
 * @author zhaozhengkang
 * @date 2023-01-10
 */
@Data
public class FtpMonthlyGuidance extends BaseModel implements Serializable, IEntity, IStateMachineEntity {

    private static final long serialVersionUID = 1L;

    /**
    * id
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
    * 年度
    */
    @TableField("year")
    private Integer year;

    /**
    * 月度
    */
    @TableField("month")
    private Integer month;

    /**
    * 审批状态
    */
    @TableField("guidance_process_status")
    private String guidanceProcessStatus;

    @TableField("guidance_record_status")
    private String guidanceRecordStatus;

    /**
    * 一年期ftp收益指导报价
    */
    @TableField("one_year_earnings_guidance")
    private Integer oneYearEarningsGuidance;

    /**
    * 1-3年期ftp收益指导报价
    */
    @TableField("one_to_three_earnings_guidance")
    private Integer oneToThreeEarningsGuidance;

    /**
    * 3年以上ftp收益指导报价
    */
    @TableField("more_than_three_earnings_guidance")
    private Integer moreThanThreeEarningsGuidance;

    /**
    * 卖出价
    */
    @TableField("selling_price")
    private Integer sellingPrice;

    /**
    * 买入价
    */
    @TableField("buying_price")
    private Integer buyingPrice;

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
    public ProcessStatus getProcessStatus(){
        return FtpProcessStatus.of(this.guidanceProcessStatus);
    }
    @Override
    public RecordStatus getRecordStatus() {
        return RecordStatus.of(this.guidanceRecordStatus);
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
