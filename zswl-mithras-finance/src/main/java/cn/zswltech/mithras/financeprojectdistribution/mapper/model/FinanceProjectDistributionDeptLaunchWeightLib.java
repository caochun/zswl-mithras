package cn.zswltech.mithras.financeprojectdistribution.mapper.model;

import cn.zswltech.mithras.service.constant.VersionTypeConstants;
import cn.zswltech.mithras.service.mapper.tag.ILib;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * @description 绩效考核-部门-项目投放分配比重版本表
 * @author lllin
 * @date2025/12/19
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("finance_project_distribution_dept_launch_weight_lib")
public class FinanceProjectDistributionDeptLaunchWeightLib extends FinanceProjectDistributionDeptLaunchWeight implements ILib {

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
