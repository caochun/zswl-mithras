package cn.zswltech.mithras.kpi.mapper.model;

import cn.zswltech.mithras.foundation.constant.VersionTypeConstants;
import cn.zswltech.mithras.foundation.persistence.tag.ILib;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * @author dingqi
 * @date 2023/6/14
 * @description 绩效考核-项目分配表（新）-分配比重信息版本表
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("kpi_project_distribution_dept_weight_lib")
public class KpiProjectDistributionDeptWeightLib extends KpiProjectDistributionDeptWeight implements ILib {
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
