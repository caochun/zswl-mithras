package cn.zswltech.mithras.afterlease.mapper.model;

import cn.zswltech.mithras.foundation.persistence.tag.ILib;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * @author dingqi
 * @date 2022/12/14
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("new_after_lease_check_report_extra_lib")
public class NewAfterLeaseCheckReportExtraLib extends NewAfterLeaseCheckReportExtra implements ILib {
    private static final long serialVersionUID = -4487225543527787666L;

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

    private Integer versionType;
}
