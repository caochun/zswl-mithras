package cn.zswltech.mithras.afterlease.model;

import cn.zswltech.mithras.afterlease.enums.AfterLeaseCheckReportAreaTypeEnum;
import cn.zswltech.mithras.afterlease.enums.AfterLeaseCheckReportTypeEnum;
import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author dingqi
 * @date 2022/11/16
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("new_after_lease_check_report_template")
public class NewAfterLeaseCheckReportTemplate extends BaseModel {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 报告类型 {@link AfterLeaseCheckReportTypeEnum#name()}
     */
    @TableField("report_type")
    private String reportType;

    /**
     * 报告区域类型 {@link AfterLeaseCheckReportAreaTypeEnum#name()}
     */
    @TableField("area_type")
    private String areaType;

    /**
     * 分组名称
     */
    @TableField("group_name")
    private String groupName;

    /**
     * 条目code
     */
    @TableField("code")
    private String code;

    /**
     * 内容条目
     */
    @TableField("title")
    private String title;

    /**
     * 内容输入框标签
     */
    @TableField("content_input_label")
    private String contentInputLabel;

    /**
     * 内容输入框类型
     */
    @TableField("content_input_type")
    private String contentInputType;

    /**
     * 内容输入框枚举类型（如果是下拉框等才会有值）
     */
    @TableField("content_input_option")
    private String contentInputOption;

    /**
     * 排序字段
     */
    @TableField("order_num")
    private Integer orderNum;
}
