package cn.zswltech.mithras.kpi.model;
import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * @description 绩效考核-项目分配表-分配比重信息记录表
 * @author vico
 * @date 2024-09-27
 */
@Data
public class KpiProjectDistributionWeightRecord extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * id
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
    * 项目分配记录表id
    */
    @TableField("project_distribution_record_id")
    private Long projectDistributionRecordId;


    /**
    * 项目分配表id
    */
    @TableField("project_distribution_id")
    private Long projectDistributionId;

    /**
    * 分配比重类型
    */
    @TableField("weight_type")
    private String weightType;

    /**
    * 分配比重归属目标
    */
    @TableField("weight_target")
    private String weightTarget;

    /**
    * 分配比重数值
    */
    @TableField("weight_value")
    private Integer weightValue;

}
