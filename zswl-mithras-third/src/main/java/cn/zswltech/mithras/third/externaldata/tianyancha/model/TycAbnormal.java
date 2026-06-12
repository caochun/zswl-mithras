package cn.zswltech.mithras.third.externaldata.tianyancha.model;

import cn.zswltech.mithras.third.externaldata.common.model.ExternalDataBaseModel;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

/**
 * @description 天眼查_经营异常
 * @author yeqing
 * @date 2022-06-21
 */
@Data
public class TycAbnormal extends ExternalDataBaseModel {

    @TableId(type = IdType.AUTO)
    /**
    * 主键
    */
    private Long id;

    /**
    * 移出日期
    */
    @TableField("remove_date")
    private String removeDate;

    /**
    * 列入异常名录原因
    */
    @TableField("put_reason")
    private String putReason;

    /**
    * 决定列⼊异常名录部⻔(作出决定机关)
    */
    @TableField("put_department")
    private String putDepartment;

    /**
    * 移出部⻔
    */
    @TableField("remove_department")
    private String removeDepartment;

    /**
    * 移除异常名录原因
    */
    @TableField("remove_reason")
    private String removeReason;

    /**
    * 列入日期
    */
    @TableField("put_date")
    private String putDate;

}