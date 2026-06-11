package cn.zswltech.mithras.metric.financialcloudmetric.model;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import cn.zswltech.mithras.foundation.util.StringUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @author zhaozhengkang
 * @description 金融云指标
 * @date 2023-04-12
 */
@Data
public class FinancialCloudMetricValue extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * metric_id
     */
    @TableField("metric_id")
    private Long metricId;

    /**
     * metric_code
     */
    @TableField("metric_code")
    private String metricCode;

    @TableField("metric_name")
    private String metricName;

    /**
     * 当期值
     */
    @TableField("metric_value")
    private String metricValue;

    /**
     * 调整后的指标值
     */
    @TableField("metric_value_adjusted")
    private String metricValueAdjusted;

    /**
     * 数据日期
     */
    @TableField("data_time")
    private LocalDate dataTime;

    /**
     * 数据来源
     */
    @TableField("data_source")
    private String dataSource;

    /**
     * 状态
     */
    @TableField("status")
    private String status;

    /**
     * need_report
     */
    @TableField("need_report")
    private Integer needReport;

    /**
     * 一级分类
     */
    @TableField("one_level_type")
    private String oneLevelType;

    /**
     * 二级分类
     */
    @TableField("two_level_type")
    private String twoLevelType;

    /**
     * 指标大类
     */
    @TableField("metric_first_type")
    private String metricFirstType;

    /**
     * 指标小类
     */
    @TableField("metric_second_type")
    private String metricSecondType;

    @TableField("frequency")
    private String frequency;

    @TableField("sort_no")
    private Integer sortNo;

    @TableField("contract_detail")
    private String contractDetail;


    public String getValue() {
        if (ObjectUtil.isNotEmpty(metricValueAdjusted)) {
            return metricValueAdjusted;
        } else {
            // metricValue是自动计算的值，只能是数字，若是文字则记录了计算错误信息，如果是数字，返回数字，否则返回null
            if (!StringUtil.isNum(metricValue)) {
                return null;
            }
        }
        return metricValue;
    }

}
