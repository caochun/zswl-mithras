package cn.zswltech.mithras.basedata.mapper.model;

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
 * @date 2022/9/16
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("base_data_lpr")
public class BaseDataLpr extends BaseModel {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    @TableField("id")
    private Long id;

    /**
     * LPR报价日
     */
    @TableField("lpr_date")
    private LocalDate lprDate;

    /**
     * 1年期LPR，单位：百分比
     */
    @TableField("one_year")
    private String oneYear;

    /**
     * 5年期LPR，单位：百分比
     */
    @TableField("five_year")
    private String fiveYear;
}
