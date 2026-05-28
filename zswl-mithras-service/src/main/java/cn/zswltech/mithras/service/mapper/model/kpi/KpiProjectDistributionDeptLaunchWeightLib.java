package cn.zswltech.mithras.service.mapper.model.kpi;
import cn.zswltech.mithras.service.constant.VersionTypeConstants;
import cn.zswltech.mithras.service.mapper.tag.ILib;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableField;
import cn.zswltech.mithras.service.mapper.model.BaseModel;
import lombok.EqualsAndHashCode;

/**
 * @description 绩效考核-部门-项目投放分配比重版本表
 * @author hspcadmin
 * @date 2025-09-29
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("kpi_project_distribution_dept_launch_weight_lib")
public class KpiProjectDistributionDeptLaunchWeightLib extends KpiProjectDistributionDeptWeight implements ILib {

    /**
     * 变更编号
     * 版本号
     */
    @TableField("version")
    private String version;

    /**
     * 临时数据表id
     * 需要用来比对数据 或者 流程拒绝时全量回写
     */
    @TableField("origin_id")
    private Long originId;

    /**
     * 记录原数据更新时间、创建时间等
     */
    @TableField("data_create_time")
    private LocalDateTime dataCreateTime;
    @TableField("data_create_by")
    private Long dataCreateBy;
    @TableField("data_update_time")
    private LocalDateTime dataUpdateTime;
    @TableField("data_update_by")
    private Long dataUpdateBy;

    /**
     * 版本标志，0无效，1有效...业务自扩展
     * {@link VersionTypeConstants}
     */
    @TableField("version_type")
    private Integer versionType;

}
