package cn.zswltech.mithras.dashboard.guanbao.model;

import cn.zswltech.mithras.dashboard.guanbao.enums.ManagementReportKeyEnum;
import cn.zswltech.mithras.dashboard.guanbao.enums.ManagementReportSourceEnum;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @create: 2023-01-04
 **/

@TableName(value ="management_report")
@Data
public class ManagementReport implements Serializable {
    /**
     * 管报id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 管报名
     */
    @TableField("report_name")
    private String reportName;

    /**
     * 管报地址
     */
    @TableField("report_url")
    private String reportUrl;

    /**
     * 管报类型，类型包括权限控制，慎用
     */
    @TableField("report_type")
    private String reportType;

    /**
     * 管报类型名称 用于分组
     */
    @TableField("report_type_name")
    private String reportTypeName;

    /**
     * 报表来源 {@link ManagementReportSourceEnum#name()}
     */
    @TableField("report_source")
    private String reportSource;

    /**
     * 报表key（用于自研页面管报的前端识别） {@link ManagementReportKeyEnum#name()}
     */
    @TableField("report_key")
    private String reportKey;

    /**
     * 排序字段
     */
    @TableField("sort_num")
    private Integer sortNum;
}
