package cn.zswltech.mithras.metric.emit.model.rsp.required;

import lombok.Data;

/**
 * @author yibin
 */
@Data
public class RequireMetricSingleBody {

    /**
     * 数据时点
     */
    private String dataTime;

    /**
     * 指标名称
     */
    private String name;

    /**
     * 指标编码
     */
    private String code;

    /**
     * 指标值
     */
    private String indexValue;

    /**
     * 指标定义
     */
    private String define;

    /**
     * 指标类型
     */
    private Integer type;

    /**
     * 指标级别
     */
    private Integer level;

    /**
     * 货币
     */
    private Integer currency;

    /**
     * 单位
     */
    private Integer unit;

    /**
     * 频率
     */
    private Integer frequency;

    /**
     * 指标类别
     */
    private Integer category;

    /**
     * 填报口径
     */
    private Integer caliber;
}
