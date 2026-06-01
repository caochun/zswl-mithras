package cn.zswltech.mithras.service.mapper.model.associationreport;

import cn.zswltech.mithras.service.enums.associationreport.AssociationProcessStatusEnum;
import cn.zswltech.mithras.service.enums.associationreport.AssociationReportStatusEnum;
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
 * @date 2025/4/21
 * @description 金融协会报送-主记录表实体类
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("association_report")
public class AssociationReport extends BaseModelWithLogicDelete {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    @TableField("id")
    private Long id;

    /**
     * 报表编码
     */
    @TableField("report_category_code")
    private String reportCategoryCode;

    /**
     * 报表名称
     */
    @TableField("report_category_name")
    private String reportCategoryName;

    /**
     * 是否为重报数据，0-否，1-是
     */
    @TableField("is_retry")
    private Integer isRetry;

    /**
     * 报表实例编号
     */
    @TableField("report_instance_id")
    private String reportInstanceId;

    /**
     * 报表实例年份
     */
    @TableField("report_year")
    private Integer reportYear;

    /**
     * 报表实例周期类型
     */
    @TableField("report_period_category")
    private String reportPeriodCategory;

    /**
     * 报表实例周期
     */
    @TableField("report_period")
    private Integer reportPeriod;

    /**
     * 数据来源（创建方式）
     */
    @TableField("data_source")
    private String dataSource;

    /**
     * 批次号
     */
    @TableField("batch_no")
    private String batchNo;

    /**
     * 上报时间
     */
    @TableField("report_time")
    private LocalDateTime reportTime;

    /**
     * 上报状态
     */
    @TableField("report_status")
    private String reportStatus;

    /**
     * 流程状态 {@link AssociationProcessStatusEnum#name()}
     */
    @TableField("process_status")
    private String processStatus;

    /**
     * 上报流程状态 {@link AssociationReportStatusEnum#name()}
     */
    @TableField("push_process_status")
    private String pushProcessStatus;

    /**
     * 是否展示在列表
     */
    @TableField("is_show")
    private Integer isShow;
}
