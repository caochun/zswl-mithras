package cn.zswltech.mithras.factory.model;

import cn.zswltech.mithras.factory.enums.RatingFetchMethodEnum;
import cn.zswltech.mithras.service.mapper.model.BaseModelWithLogicDelete;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * @author dingqi
 * @date 2025/3/18
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("rating_client_area_indicator")
public class RatingClientAreaIndicator extends BaseModelWithLogicDelete {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    @TableField(value = "id")
    private Long id;

    /**
     * 客户评级记录id
     */
    @TableField(value = "rating_client_id")
    private Long ratingClientId;

    /**
     * 区域编码
     */
    @TableField(value = "area_uni_code")
    private Long areaUniCode;

    /**
     * 年份
     */
    @TableField(value = "year")
    private Integer year;

    /**
     * 区域名称
     */
    @TableField(value = "area_name")
    private String areaName;

    /**
     * 类别编码 {@link RatingClientAreaIndicatorConfig.CategoryCodeEnum#name()}
     */
    @TableField(value = "category_code")
    private String categoryCode;

    /**
     * 类别名称
     */
    @TableField(value = "category_name")
    private String categoryName;

    /**
     * 指标编码 {@link RatingClientAreaIndicatorConfig.DmIndicatorCode#name()}
     */
    @TableField(value = "indicator_code")
    private String indicatorCode;

    /**
     * 指标数值
     */
    @TableField(value = "indicator_value")
    private BigDecimal indicatorValue;

    /**
     * 指标数值（系统初始化）
     */
    @TableField(value = "indicator_value_system")
    private BigDecimal indicatorValueSystem;

    /**
     * 指标名称
     */
    @TableField(value = "indicator_name")
    private String indicatorName;

    /**
     * 取数方式 {@link RatingFetchMethodEnum#name()}
     */
    @TableField(value = "mode")
    private String mode;

    /**
     * 指标单位
     */
    @TableField(value = "indicator_unit")
    private String indicatorUnit;

    /**
     * 指标数据类型
     */
    @TableField(value = "indicator_data_type")
    private String indicatorDataType;

    /**
     * 指标排序
     */
    @TableField(value = "indicator_sort")
    private Integer indicatorSort;
}
