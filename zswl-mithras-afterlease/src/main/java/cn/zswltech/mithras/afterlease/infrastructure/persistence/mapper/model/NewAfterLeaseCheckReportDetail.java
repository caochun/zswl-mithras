package cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.model;

import cn.zswltech.mithras.afterlease.domain.enums.SaveStatusEnum;
import cn.zswltech.mithras.service.mapper.model.BaseModel;
import cn.zswltech.mithras.service.mapper.tag.IEntity;
import cn.zswltech.mithras.service.plugin.IncludeNull;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author dingqi
 * @date 2023/11/14
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("new_after_lease_check_report_detail")
public class NewAfterLeaseCheckReportDetail extends BaseModel implements IEntity {
    @TableId(type = IdType.AUTO)
    @TableField(value = "id")
    private Long id;

    /**
     * 检查计划-客户信息表主键id
     */
    @TableField(value = "check_plan_client_id")
    private Long checkPlanClientId;

    /**
     * 检查报告内容
     */
    @IncludeNull
    @TableField(value = "report_content")
    private String reportContent;

    /**
     * 检查报告总结
     */
    @IncludeNull
    @TableField(value = "report_summary")
    private String reportSummary;

    /**
     * 报告总结保存状态 {@link SaveStatusEnum}
     */
    @TableField(value = "report_content_save_status")
    private String reportContentSaveStatus;

    /**
     * 报告内容保存状态 {@link SaveStatusEnum}
     */
    @TableField(value = "report_summary_save_status")
    private String reportSummarySaveStatus;

    @Override
    public void setMainId(Long id) {
        this.checkPlanClientId = id;
    }

    @Override
    public Long getMainId() {
        return this.checkPlanClientId;
    }

    @Data
    public static class FieldData {
        // 0代表这个字段不会重复，大于0代表可以添加模块使得该字段重复出现
        private Integer moduleIndex;
        private String fieldName;
        private String fieldRemark;
        private String fieldValue;
        private String fieldType;
        private List<OptionData> fieldOption;
        private String attributionList;
    }
}
