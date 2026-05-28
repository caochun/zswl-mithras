package cn.zswltech.mithras.factory.model;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 区域信息表
 * </p>
 *
 * @author chenyifei
 * @since 2024-06-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("area_info")
public class AreaInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 区域编码
     */
    @TableField("area_uni_code")
    private Long areaUniCode;

    /**
     * 区域名称
     */
    @TableField("area_name")
    private String areaName;

    /**
     * 父类区域编码
     */
    @TableField("parent_area_uni_code")
    private Long parentAreaUniCode;

    /**
     * 上级地区名称
     */
    @TableField("parent_area_name")
    private String parentAreaName;

    /**
     * 省编码
     */
    @TableField("province_uni_code")
    private Long provinceUniCode;

    /**
     * 省名称
     */
    @TableField("province_name")
    private String provinceName;

    /**
     * 市编码
     */
    @TableField("city_uni_code")
    private Long cityUniCode;

    /**
     * 市名称
     */
    @TableField("city_name")
    private String cityName;

    /**
     * 区县编码
     */
    @TableField("district_uni_code")
    private Long districtUniCode;

    /**
     * 区县名称
     */
    @TableField("district_name")
    private String districtName;

    /**
     * 行政级别id
     */
    @TableField("administrative_region")
    private Integer administrativeRegion;

    /**
     * 行政级别名称
     */
    @TableField("administrative_region_name")
    private String administrativeRegionName;

    /**
     * 地区行政编码
     */
    @TableField("administrative_code")
    private Integer administrativeCode;

    /**
     * 区域类型
     */
    @TableField("area_type")
    private Integer areaType;

    /**
     * 园区创建时间描述
     */
    @TableField("zone_establish_time_desc")
    private String zoneEstablishTimeDesc;

    /**
     * 经度
     */
    @TableField("longitude")
    private BigDecimal longitude;

    /**
     * 纬度
     */
    @TableField("latitude")
    private BigDecimal latitude;

    /**
     * 是否删除
     */
    @TableField("deleted")
    private Integer deleted;

    /**
     * 更新时间id
     */
    @TableField("update_time_id")
    private Long updateTimeId;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;


}
