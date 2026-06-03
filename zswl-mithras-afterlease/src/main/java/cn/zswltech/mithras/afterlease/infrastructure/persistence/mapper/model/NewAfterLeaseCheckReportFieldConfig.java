package cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.model;

import cn.zswltech.mithras.service.mapper.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author dingqi
 * @date 2023/11/14
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("new_after_lease_check_report_field_config")
public class NewAfterLeaseCheckReportFieldConfig extends BaseModel {
    @TableId(type = IdType.AUTO)
    @TableField(value = "id")
    private Long id;

    @TableField(value = "report_type")
    private String reportType;

    @TableField(value = "field_name")
    private String fieldName;

    @TableField(value = "field_type")
    private String fieldType;

    /**
     * {@link OptionData}
     */
    @TableField(value = "field_option")
    private String fieldOption;

    @TableField(value = "field_remark")
    private String fieldRemark;
}
