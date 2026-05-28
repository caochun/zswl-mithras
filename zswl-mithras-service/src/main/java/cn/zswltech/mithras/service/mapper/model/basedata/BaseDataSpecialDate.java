package cn.zswltech.mithras.service.mapper.model.basedata;

import cn.zswltech.mithras.service.enums.basedata.BaseDataSpecialDateTypeEnum;
import cn.zswltech.mithras.service.mapper.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2022/9/24
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("base_data_special_date")
public class BaseDataSpecialDate extends BaseModel {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    @TableField("id")
    private Long id;

    /**
     * 年份
     */
    @TableField("year")
    private Integer year;

    /**
     * 月份
     */
    @TableField("month")
    private Integer month;

    /**
     * 日期
     */
    @TableField("special_date")
    private LocalDate specialDate;

    /**
     * 特殊类型 {@link BaseDataSpecialDateTypeEnum#name()}
     */
    @TableField("special_type")
    private String specialType;
}
