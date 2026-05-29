package cn.zswltech.mithras.service.mapper.model.workbench;

import cn.zswltech.mithras.common.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @description 工作台-项目整体收益率指标
 * @author zhaozhengkang
 * @date 2023-05-09
 */
@Data
public class WorkbenchOverallReturnRateMetric extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * id
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 指标月份
     */
    @TableField("month")
    private LocalDate month;

    /**
    * 部门范围
    */
    @TableField("dept_socp")
    private String deptSocp;

    /**
    * 项目类型
    */
    @TableField("project_type")
    private String projectType;

    /**
    * 值， 单位%
    */
    @TableField("value")
    private String value;

}
